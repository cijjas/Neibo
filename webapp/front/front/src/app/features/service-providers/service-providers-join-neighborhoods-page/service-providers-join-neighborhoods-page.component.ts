import {
  Component,
  ElementRef,
  ViewChild,
  OnInit,
  AfterViewInit,
  HostListener,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  HateoasLinksService,
  ToastService,
  UserSessionService,
} from '@core/index';
import { AffiliationService, NeighborhoodService } from '@shared/index';
import { Affiliation, LinkKey, Neighborhood } from '@shared/models';

@Component({
  selector: 'app-neighborhoods',
  templateUrl: './service-providers-join-neighborhoods-page.component.html',
})
export class ServiceProvidersJoinNeighborhoodsComponent
  implements OnInit, AfterViewInit
{
  // Simulates the data from your JSP
  associatedNeighborhoods: Affiliation[] = [];
  otherNeighborhoods: Neighborhood[] = [];

  // Tracks the neighborhood IDs that the user selects
  selectedNeighborhoodIds: string[] = [];

  // Whether the drop-down is open or not
  isSelectOpen = false;

  // --- NEW: for pagination ---
  page = 1; // current page
  size = 10; // items to fetch per page
  hasMore = true; // indicates if more pages are available
  isLoading = false; // to prevent multiple simultaneous fetches

  currentAssociatedPage = 1; // Current page for affiliated neighborhoods
  totalAssociatedPages = 1; // Total pages available
  pageSize = 10; // Page size (items per page)

  // For referencing DOM elements
  @ViewChild('selectBtn') selectBtnRef!: ElementRef;
  // --- NEW: reference to the scrollable UL ---
  @ViewChild('listItems', { static: false })
  listItemsRef!: ElementRef<HTMLUListElement>;
  isListenerAttached = false;

  constructor(
    private neighborhoodService: NeighborhoodService,
    private linkService: HateoasLinksService,
    private toastService: ToastService,
    private affiliationService: AffiliationService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.currentAssociatedPage = +params['page'] || 1; // Default to page 1
      this.pageSize = +params['size'] || 10; // Default to size 10
      this.loadAssociatedNeighborhoods();
      this.loadOtherNeighborhoods(true);
    });
  }

  // --- NEW: attach the scroll listener after view init ---
  ngAfterViewInit(): void {
    if (this.listItemsRef) {
      this.listItemsRef.nativeElement.addEventListener(
        'scroll',
        this.onListScroll.bind(this)
      );
    }
  }

  ngAfterViewChecked(): void {
    if (!this.listItemsRef || !this.listItemsRef.nativeElement) {
      // console.log('ListItemsRef not yet defined');
      return;
    }

    if (!this.isListenerAttached) {
      console.log('Attaching scroll listener to listItemsRef');
      this.listItemsRef.nativeElement.addEventListener(
        'scroll',
        this.onListScroll.bind(this)
      );
      this.isListenerAttached = true; // Prevent reattaching
    }
  }

  // --- Existing logic (unchanged) ---
  loadAssociatedNeighborhoods(): void {
    const queryParams = {
      forWorker: this.linkService.getLink(LinkKey.USER_WORKER),
      page: this.currentAssociatedPage,
      size: this.pageSize,
    };

    this.affiliationService.getAffiliations(queryParams).subscribe({
      next: (response) => {
        this.associatedNeighborhoods = response.affiliations;
        this.totalAssociatedPages = response.totalPages || 1; // Total pages from API response
        this.updateQueryParams(); // Persist the current page in the URL
      },
      error: () => {
        this.toastService.showToast(
          'Could not load associated neighborhoods.',
          'error'
        );
      },
    });
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.currentAssociatedPage, size: this.pageSize },
      queryParamsHandling: 'merge', // Preserve other query params
    });
  }

  onAssociatedPageChange(page: number): void {
    this.currentAssociatedPage = page;
    this.updateQueryParams();
    this.loadAssociatedNeighborhoods();
  }

  // --- Modified to handle paging ---
  loadOtherNeighborhoods(isFirstPage = false): void {
    if (isFirstPage) {
      // Reset state for new
      this.resetScrollState();
      this.page = 1;
      this.otherNeighborhoods = [];
      this.hasMore = true;
    }

    // If we have no more results or are already loading, do nothing
    if (!this.hasMore || this.isLoading) return;

    this.isLoading = true;

    const queryParams = {
      withoutWorker: this.linkService.getLink(LinkKey.USER_WORKER),
      page: this.page,
      size: this.size,
    };

    this.neighborhoodService.getNeighborhoods(queryParams).subscribe({
      next: (response) => {
        const newItems = response.neighborhoods ?? [];
        // Append new items to existing
        this.otherNeighborhoods.push(...newItems);

        // If we got fewer items than 'size', assume we're at the last page
        if (newItems.length < this.size) {
          this.hasMore = false;
        } else {
          this.page++;
        }

        this.isLoading = false;
      },
      error: () => {
        this.toastService.showToast(
          'Could not load unassociated neighborhoods.',
          'error'
        );
        this.isLoading = false;
      },
    });
  }

  // --- NEW: Handler for the scroll event on UL .list-items.n-workers ---
  onListScroll(event: Event): void {
    const element = event.target as HTMLElement;

    // If the user is near the bottom, attempt to load more
    const threshold = 30; // adjust to your liking
    const position =
      element.scrollHeight - element.scrollTop - element.clientHeight;

    if (position < threshold) {
      this.loadOtherNeighborhoods(false);
    }
  }

  onRemoveNeighborhood(neighborhood: Neighborhood): void {
    this.affiliationService.deleteAffiliation(neighborhood.self).subscribe({
      next: () => {
        this.associatedNeighborhoods = this.associatedNeighborhoods.filter(
          (affiliation) => affiliation.neighborhood.self !== neighborhood.self
        );
        // After removal, reload the list (optional if you prefer)
        this.loadOtherNeighborhoods(true);
        this.toastService.showToast(
          `You have successfully left '${neighborhood.name}'.`,
          'success'
        );
      },
      error: (err) => {
        console.error(
          `Failed to unaffiliate from '${neighborhood.name}':`,
          err
        );
        this.toastService.showToast(
          `Failed to leave '${neighborhood.name}'. Please try again.`,
          'error'
        );
      },
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
   * Displays text for selected neighborhoods
   */
  get displayText(): string {
    if (this.selectedNeighborhoodIds.length === 0) {
      return 'Select neighborhood';
    }
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

  onSubmit(): void {
    this.affiliationService
      .createAffiliations(this.selectedNeighborhoodIds)
      .subscribe({
        next: () => {
          this.toastService.showToast(
            'Affiliations created successfully!',
            'success'
          );

          // Reload associated neighborhoods
          this.loadAssociatedNeighborhoods();

          // Reset and reload other neighborhoods
          this.resetScrollState();
          this.loadOtherNeighborhoods(true);

          // Clear selected IDs
          this.selectedNeighborhoodIds = [];

          // Reinitialize scroll listener after reload
          this.reinitializeScrollListener();
        },
        error: () => {
          this.toastService.showToast(
            'Affiliations to neighborhoods could not be done, try again later.',
            'error'
          );
        },
      });
  }

  resetScrollState(): void {
    this.page = 0;
    this.hasMore = true;
    this.isLoading = false;
    this.isListenerAttached = false;
  }

  reinitializeScrollListener(): void {
    setTimeout(() => {
      if (!this.listItemsRef || !this.listItemsRef.nativeElement) {
        // console.log('ListItemsRef not yet defined for reinitialization');
        return;
      }

      if (!this.isListenerAttached) {
        // console.log('Reattaching scroll listener to listItemsRef');
        this.listItemsRef.nativeElement.addEventListener(
          'scroll',
          this.onListScroll.bind(this)
        );
        this.isListenerAttached = true; // Prevent duplicate listeners
      }
    }, 0); // Delay to ensure DOM is updated
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
