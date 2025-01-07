import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, ToastService } from '@core/index';
import { Neighborhood, NeighborhoodService } from '@shared/index';

@Component({
  selector: 'app-rejected-page',
  templateUrl: './rejected-page.component.html',
})
export class RejectedPageComponent implements OnInit {
  @ViewChild('neighborhoodSelect') neighborhoodSelect!: ElementRef; // Reference to the dropdown
  darkMode = false; // Replace with a proper service or state for dark mode
  neighborhoodsList: Neighborhood[] = [];
  currentPage = 0;
  totalPages = 0;
  isLoading = false;

  neighborhoodForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private neighborhoodService: NeighborhoodService,
    private toastService: ToastService,
    private authService: AuthService,
    private router: Router
  ) {
    this.neighborhoodForm = this.fb.group({
      neighborhoodId: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.loadNeighborhoods();
  }

  loadNeighborhoods(): void {
    if (this.isLoading || this.currentPage >= this.totalPages) return;

    this.isLoading = true;
    this.neighborhoodService
      .getNeighborhoods({ page: this.currentPage, size: 20 }) // Fetch 20 neighborhoods per page
      .subscribe({
        next: (response) => {
          this.neighborhoodsList = [
            ...this.neighborhoodsList,
            ...response.neighborhoods,
          ];
          this.currentPage = response.currentPage + 1;
          this.totalPages = response.totalPages;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error getting neighborhoods:', error);
          this.toastService.showToast(
            'There was a problem fetching the neighborhoods. Try again.',
            'error'
          );
          this.isLoading = false;
        },
      });
  }

  onDropdownScroll(): void {
    const selectElement = this.neighborhoodSelect.nativeElement;
    const threshold = 100; // Distance from the bottom to trigger loading

    if (
      selectElement.scrollTop + selectElement.clientHeight >=
      selectElement.scrollHeight - threshold
    ) {
      this.loadNeighborhoods();
    }
  }

  onSubmit(): void {
    if (this.neighborhoodForm.valid) {
      console.log('Form Submitted:', this.neighborhoodForm.value);
      // Add service call logic here for form submission
    } else {
      console.error('Form is invalid');
    }
  }
  goBackToMainPage(): void {
    // Example: log out and navigate to root or login
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
