// rejected-page.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, ToastService } from '@core/index';
import { Neighborhood, NeighborhoodService, UserService } from '@shared/index';
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
    this.submitted = true; // Mark form as submitted

    if (this.neighborhoodForm.valid) {
      const selectedNeighborhood: Neighborhood =
        this.neighborhoodForm.value.neighborhood;
      console.log(
        'Form Submitted:',
        selectedNeighborhood.name,
        selectedNeighborhood.self
      );

      //this.userService.update
      // Add service call logic here for form submission
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
