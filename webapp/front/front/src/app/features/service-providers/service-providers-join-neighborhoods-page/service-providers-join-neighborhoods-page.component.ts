import { Component, ElementRef, ViewChild, OnInit, HostListener } from '@angular/core';
import { HateoasLinksService, ToastService, UserSessionService } from '@core/index';
import { AffiliationService, NeighborhoodService } from '@shared/index';
import { Affiliation, LinkKey, Neighborhood } from '@shared/models';


@Component({
  selector: 'app-neighborhoods',
  templateUrl: './service-providers-join-neighborhoods-page.component.html',
})
export class ServiceProvidersJoinNeighborhoodsComponent implements OnInit {
  darkMode = false;

  // Simulates the data from your JSP
  associatedNeighborhoods: Affiliation[] = [];
  otherNeighborhoods: Neighborhood[] = [];

  // Tracks the neighborhood IDs that the user selects
  selectedNeighborhoodIds: string[] = [];

  // Whether the drop-down is open or not
  isSelectOpen = false;

  // For referencing DOM elements
  @ViewChild('selectBtn') selectBtnRef!: ElementRef;

  constructor(
    private neighborhoodService: NeighborhoodService,
    private linkService: HateoasLinksService,
    private toastService: ToastService,
    private affiliationService: AffiliationService
  ) { }

  ngOnInit(): void {
    this.loadAssociatedNeighborhoods();
    this.loadOtherNeighborhoods();
    this.updateDisplayText();
  }

  loadAssociatedNeighborhoods(): void {
    const queryParams = {
      forWorker: this.linkService.getLink(LinkKey.USER_WORKER)
    }

    this.affiliationService.getAffiliations(queryParams).subscribe({
      next: (response) => {
        this.associatedNeighborhoods = response.affiliations;
      },
      error: () => {
        this.toastService.showToast('Could not load associated neighborhoods.', 'error');

      }
    })
  }

  loadOtherNeighborhoods(): void {
    const queryParams = {
      withoutWorker: this.linkService.getLink(LinkKey.USER_WORKER)
    }

    this.neighborhoodService.getNeighborhoods(queryParams).subscribe({
      next: (response) => {
        this.otherNeighborhoods = response.neighborhoods;
      },
      error: (error) => {
        this.toastService.showToast('Could not load unassociated neighborhoods.', 'error');

      }
    })
  }
  onRemoveNeighborhood(neighborhood: Neighborhood): void {
    this.affiliationService.deleteAffiliation(neighborhood.self).subscribe({
      next: () => {
        this.associatedNeighborhoods = this.associatedNeighborhoods.filter(
          (affiliation) => affiliation.neighborhood.self !== neighborhood.self
        );
        this.loadOtherNeighborhoods();
        this.toastService.showToast(`You have successfully left '${neighborhood.name}'.`, 'success');
      },
      error: (err) => {
        console.error(`Failed to unaffiliate from '${neighborhood.name}':`, err);
        this.toastService.showToast(`Failed to leave '${neighborhood.name}'. Please try again.`, 'error');
      }
    });
  }

  /**
   * Handles toggling an item in the "otherNeighborhoods" drop-down
   */
  toggleItem(neighborhoodId: string, name: string, event: MouseEvent): void {
    event.stopPropagation(); // So we don’t also trigger toggleSelect()

    if (this.selectedNeighborhoodIds.includes(neighborhoodId)) {
      // Remove it
      this.selectedNeighborhoodIds = this.selectedNeighborhoodIds.filter(
        (id) => id !== neighborhoodId
      );
    } else {
      // Add it
      this.selectedNeighborhoodIds.push(neighborhoodId);
    }

    // Update the displayed text with newly selected items
    this.updateDisplayText();
  }

  /**
   * Updates the text shown on the select button
   */
  get displayText(): string {
    if (this.selectedNeighborhoodIds.length === 0) {
      return 'Select neighborhood';
    }
    // Optionally show them all, or abbreviate
    const selected = this.otherNeighborhoods
      .filter((o) => this.selectedNeighborhoodIds.includes(o.self))
      .map((o) => o.name);

    if (selected.length > 1) {
      return `(${selected.length}) ${selected.join(', ')}`;
    } else {
      return selected.join(', ');
    }
  }

  updateDisplayText(): void {
    // Forces the template to recompute 'displayText'
  }

  /**
   * Opens/closes the drop-down list
   */
  toggleSelect(): void {
    this.isSelectOpen = !this.isSelectOpen;
  }

  /**
   * Submit action for the form
   */
  onSubmit(): void {
    this.affiliationService.createAffiliations(this.selectedNeighborhoodIds).subscribe({
      next: () => {
        this.toastService.showToast('Affiliations created successfully!', 'success');
        this.loadAssociatedNeighborhoods(); // Reload associated neighborhoods
        this.loadOtherNeighborhoods(); // Optionally reload other neighborhoods if needed
        this.selectedNeighborhoodIds = []; // Clear the selected IDs after submission
      },
      error: () => {
        this.toastService.showToast('Affiliations to neighborhoods could not be done, try again later.', 'error');
      }
    });
  }

  /**
   * Clicking outside the drop-down will close it, similar to your JSP script logic
   */
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    // If the click is outside the .select-btn or its children, close the list
    if (
      this.isSelectOpen &&
      this.selectBtnRef &&
      !this.selectBtnRef.nativeElement.contains(event.target)
    ) {
      this.isSelectOpen = false;
    }
  }
}