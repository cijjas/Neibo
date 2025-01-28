// rejected-page.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, ToastService, UserSessionService } from '@core/index';
import {
  Neighborhood,
  NeighborhoodService,
  Roles,
  UserService,
} from '@shared/index';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-rejected-page',
  templateUrl: './rejected-page.component.html',
})
export class RejectedPageComponent implements OnInit {
  neighborhoodForm: FormGroup;
  isLoading = false;
  submitted = false;

  constructor(
    private fb: FormBuilder,
    private neighborhoodService: NeighborhoodService,
    private toastService: ToastService,
    private authService: AuthService,
    private router: Router,
    private userService: UserService,
    private userSessionService: UserSessionService
  ) {
    this.neighborhoodForm = this.fb.group({
      neighborhood: [null, Validators.required],
    });
  }

  ngOnInit(): void {
    // Initializations if needed
  }

  fetchNeighborhoods = (page: number, size: number): Observable<any> => {
    return this.neighborhoodService.getNeighborhoods({ page, size }).pipe(
      map((response) => ({
        items: response.neighborhoods,
        currentPage: response.currentPage,
        totalPages: response.totalPages,
      }))
    );
  };

  displayNeighborhood = (neighborhood: Neighborhood): string => {
    return neighborhood.name;
  };

  /**
   * Handle form submission.
   */
  onSubmit(): void {
    this.submitted = true;

    if (this.neighborhoodForm.valid) {
      const selectedNeighborhood: Neighborhood =
        this.neighborhoodForm.value.neighborhood;

      this.userService
        .requestNeighborhood(selectedNeighborhood.self)
        .subscribe({
          next: () => {
            // Update the user role to "UNVERIFIED"
            this.userSessionService.updateUserProperty(
              'userRole',
              Roles.UNVERIFIED_NEIGHBOR
            );
            this.userSessionService.updateUserProperty(
              'userRoleEnum',
              Roles.UNVERIFIED_NEIGHBOR
            );
            this.userSessionService.updateUserProperty(
              'userRoleDisplay',
              'Unverified'
            );

            // Update standalone currentUserRole in local storage
            this.userSessionService.setUserRole(Roles.UNVERIFIED_NEIGHBOR);

            // Show success message
            this.toastService.showToast(
              'Successfully requested neighborhood to join! Wait until an administrator approves your request.',
              'success'
            );

            // Optional: Reload or navigate as needed
            this.router.navigate(['/unverified']);
          },
          error: () => {
            this.toastService.showToast(
              'There was an error sending your requested neighborhood to join!',
              'error'
            );
          },
        });
    } else {
      this.toastService.showToast('Please select a neighborhood.', 'error');
    }
  }

  /**
   * Navigate back to the main page or login.
   */
  goBackToMainPage(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
