import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HateoasLinksService } from '@core/index';
import { NeighborhoodService, Neighborhood } from '@shared/index';

@Component({
  selector: 'app-join-neighborhoods',
  templateUrl: './service-providers-join-neighborhoods-page.component.html',
})
export class ServiceProvidersJoinNeighborhoodsComponent implements OnInit {
  darkMode = false;

  // Arrays for "my" vs. "other" neighborhoods
  associatedNeighborhoods: Neighborhood[] = [];
  otherNeighborhoods: Neighborhood[] = [];
  allNeighborhoods: Neighborhood[] = [];

  neighborhoodsForm!: FormGroup;

  // Track which neighborhoods the user selects from "otherNeighborhoods"
  selectedNeighborhoodIds: string[] = [];

  // Infinite scroll
  currentPage = 1;
  totalPages = 1;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private neighborhoodService: NeighborhoodService,
    private linkService: HateoasLinksService
  ) { }

  ngOnInit(): void {
    // Set up form
    this.neighborhoodsForm = this.fb.group({
      neighborhoodIds: [''] // will hold the list of selected IDs, comma-separated if needed
    });

    // Load existing (joined) neighborhoods
    this.loadAssociatedNeighborhoods();

    // Load first page of “other” neighborhoods
    this.loadOtherNeighborhoods(this.currentPage);
  }

  /**
   * Loads the neighborhoods the user is already associated with.
   */
  loadAssociatedNeighborhoods(): void {
    let neighborhoodsUrl: string = this.linkService.getLink('neighborhood:neighborhoods')
    this.neighborhoodService.getNeighborhoods(neighborhoodsUrl).subscribe({
      next: (data) => {
        this.allNeighborhoods = data.neighborhoods;
      },
      error: (err) => console.error(err)
    });

    let workerUrl: string = this.linkService.getLink('user:worker')
    this.neighborhoodService.getNeighborhoods(neighborhoodsUrl, { withWorker: workerUrl }).subscribe({
      next: (data) => {
        this.associatedNeighborhoods = data.neighborhoods;
      },
      error: (err) => console.error(err)
    });
  }

  /**
   * Loads neighborhoods not yet joined, possibly paginated.
   */
  loadOtherNeighborhoods(page: number): void {
    if (this.isLoading) return;

    this.isLoading = true;
    const url = '/api/neighborhoods/other'; // Adjust to your endpoint

    this.neighborhoodService
      .getNeighborhoods(url, { page, size: 10 })  // example query params
      .subscribe({
        next: (data) => {
          // If page=1, reset the array, else push
          if (page === 1) {
            this.otherNeighborhoods = data.neighborhoods;
          } else {
            this.otherNeighborhoods = [
              ...this.otherNeighborhoods,
              ...data.neighborhoods
            ];
          }

          this.currentPage = data.currentPage;
          this.totalPages = data.totalPages;
          this.isLoading = false;
        },
        error: (err) => {
          console.error(err);
          this.isLoading = false;
        }
      });
  }

  /**
   * Toggle the selection of a neighborhood from the "otherNeighborhoods" list.
   */
  toggleNeighborhoodSelection(neighborhood: Neighborhood): void {
    const index = this.selectedNeighborhoodIds.indexOf(neighborhood.self);
    if (index > -1) {
      // Already selected => deselect
      this.selectedNeighborhoodIds.splice(index, 1);
    } else {
      // Not selected => add
      this.selectedNeighborhoodIds.push(neighborhood.self);
    }
    // Update the form control value
    this.neighborhoodsForm
      .get('neighborhoodIds')
      ?.setValue(this.selectedNeighborhoodIds);
  }

  /**
   * Removes one neighborhood from the user's associated list.
   */
  removeNeighborhood(entry: Neighborhood): void {
    // Example: call the service to remove the neighborhood
    // Adjust the endpoint or logic as needed
    const removeUrl = entry.self; // or something like `/api/neighborhoods/remove/${id}`
    // For demonstration:
    /*
    this.neighborhoodService.deleteNeighborhood(removeUrl).subscribe({
      next: () => {
        // remove from the UI list
        this.associatedNeighborhoods = this.associatedNeighborhoods.filter(
          (nh) => nh.self !== entry.self
        );
      },
      error: (err) => console.error(err)
    });
    */
    console.log('Removing neighborhood:', entry.name);
  }

  /**
   * Submits the selected neighborhoods to join.
   */
  onSubmit(): void {
    if (this.neighborhoodsForm.valid && this.selectedNeighborhoodIds.length > 0) {
      // Example: /api/neighborhoods/join
      console.log('Joining neighborhoods:', this.selectedNeighborhoodIds);
      /*
      this.neighborhoodService.joinNeighborhoods(this.selectedNeighborhoodIds).subscribe({
        next: () => {
          // Possibly refresh the lists
          this.loadAssociatedNeighborhoods();
          this.loadOtherNeighborhoods(1);
          this.selectedNeighborhoodIds = [];
          this.neighborhoodsForm.reset();
        },
        error: (err) => console.error(err)
      });
      */
    } else {
      // Mark as touched or show an error
      this.neighborhoodsForm.markAllAsTouched();
    }
  }

  /**
   * Loads more neighborhoods on scroll, if not on the last page.
   */
  onScroll(event: Event): void {
    const target = event.target as HTMLElement;
    const threshold = 100;
    if (
      !this.isLoading &&
      this.currentPage < this.totalPages &&
      target.scrollHeight - target.scrollTop - target.clientHeight < threshold
    ) {
      this.loadOtherNeighborhoods(this.currentPage + 1);
    }
  }
}
