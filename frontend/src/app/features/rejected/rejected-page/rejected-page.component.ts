// rejected-page.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, ToastService, UserSessionService } from '@core/index';
import {
  Neighborhood,
  NeighborhoodService,
  Role,
  UserService,
} from '@shared/index';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';

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
    private translate: TranslateService,
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
              Role.UNVERIFIED_NEIGHBOR
            );
            this.userSessionService.updateUserProperty(
              'userRoleEnum',
              Role.UNVERIFIED_NEIGHBOR
            );
            this.userSessionService.updateUserProperty(
              'userRoleDisplay',
              'Unverified'
            );

            // Update standalone currentUserRole in local storage
            this.userSessionService.setUserRole(Role.UNVERIFIED_NEIGHBOR);

            // Show success message
            this.toastService.showToast(
              this.translate.instant('REJECTED-PAGE.SUCCESSFULLY_REQUESTED'),
              'success'
            );

            // Optional: Reload or navigate as needed
            this.router.navigate(['/unverified']);
          },
          error: () => {
            this.toastService.showToast(
              this.translate.instant('REJECTED-PAGE.ERROR_REQUESTED'),
              'error'
            );
          },
        });
    } else {
      this.toastService.showToast(this.translate.instant('REJECTED-PAGE.PLEASE_SELECT_A_NEIGHBORHOOD'), 'error');
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
