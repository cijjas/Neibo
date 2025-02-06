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
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-neighborhoods',
  templateUrl: './service-providers-join-neighborhoods-page.component.html',
})
export class ServiceProvidersJoinNeighborhoodsPageComponent
  implements OnInit, AfterViewInit
{
  associatedNeighborhoods: Affiliation[] = [];
  otherNeighborhoods: Neighborhood[] = [];

  selectedNeighborhoodIds: string[] = [];

  isSelectOpen = false;

  page = 1; 
  size = 10; 
  hasMore = true; 
  isLoading = false; 

  currentAssociatedPage = 1; 
  totalAssociatedPages = 1; 
  pageSize = 10; 

  @ViewChild('selectBtn') selectBtnRef!: ElementRef;
  @ViewChild('listItems', { static: false })
  listItemsRef!: ElementRef<HTMLUListElement>;
  isListenerAttached = false;

  constructor(
    private neighborhoodService: NeighborhoodService,
    private linkService: HateoasLinksService,
    private toastService: ToastService,
    private affiliationService: AffiliationService,
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.currentAssociatedPage = +params['page'] || 1; 
      this.pageSize = +params['size'] || 10; 
      this.loadAssociatedNeighborhoods();
      this.loadOtherNeighborhoods(true);
    });
  }

  ngAfterViewInit(): void {
    if (this.listItemsRef) {
      this.listItemsRef.nativeElement.addEventListener(
        'scroll',
        this.onListScroll.bind(this),
      );
    }
  }

  ngAfterViewChecked(): void {
    if (!this.listItemsRef || !this.listItemsRef.nativeElement) {
      return;
    }

    if (!this.isListenerAttached) {
      this.listItemsRef.nativeElement.addEventListener(
        'scroll',
        this.onListScroll.bind(this),
      );
      this.isListenerAttached = true; 
    }
  }

  loadAssociatedNeighborhoods(): void {
    const queryParams = {
      forWorker: this.linkService.getLink(LinkKey.USER_WORKER),
      page: this.currentAssociatedPage,
      size: this.pageSize,
    };

    this.affiliationService.getAffiliations(queryParams).subscribe({
      next: response => {
        this.associatedNeighborhoods = response.affiliations;
        this.totalAssociatedPages = response.totalPages || 1; 
        this.updateQueryParams(); 
      },
      error: () => {
        this.toastService.showToast(
          this.translate.instant(
            'SERVICE-PROVIDERS-JOIN-NEIGHBORHOODS-PAGE.COULD_NOT_LOAD_ASSOCIATED_NEIGHBORHOODS',
          ),
          'error',
        );
      },
    });
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.currentAssociatedPage, size: this.pageSize },
      queryParamsHandling: 'merge', 
    });
  }

  onAssociatedPageChange(page: number): void {
    this.currentAssociatedPage = page;
    this.updateQueryParams();
    this.loadAssociatedNeighborhoods();
  }

  loadOtherNeighborhoods(isFirstPage = false): void {
    if (isFirstPage) {
      this.resetScrollState();
      this.page = 1;
      this.otherNeighborhoods = [];
      this.hasMore = true;
    }

    if (!this.hasMore || this.isLoading) return;

    this.isLoading = true;

    const queryParams = {
      withoutWorker: this.linkService.getLink(LinkKey.USER_WORKER),
      page: this.page,
      size: this.size,
    };

    this.neighborhoodService.getNeighborhoods(queryParams).subscribe({
      next: response => {
        const newItems = response.neighborhoods ?? [];
        this.otherNeighborhoods.push(...newItems);

        if (newItems.length < this.size) {
          this.hasMore = false;
        } else {
          this.page++;
        }

        this.isLoading = false;
      },
      error: () => {
        this.toastService.showToast(
          this.translate.instant(
            'SERVICE-PROVIDERS-JOIN-NEIGHBORHOODS-PAGE.COULD_NOT_LOAD_UNASSOCIATED_NEIGHBORHOODS',
          ),
          'error',
        );
        this.isLoading = false;
      },
    });
  }

  onListScroll(event: Event): void {
    const element = event.target as HTMLElement;

    const threshold = 30; 
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
          affiliation => affiliation.neighborhood.self !== neighborhood.self,
        );
        this.loadOtherNeighborhoods(true);
        this.toastService.showToast(
          this.translate.instant(
            'SERVICE-PROVIDERS-JOIN-NEIGHBORHOODS-PAGE.YOU_HAVE_SUCCESSFULLY_LEFT',
            { neighborhood: neighborhood.name },
          ),
          'success',
        );
      },
      error: err => {
        console.error(
          this.translate.instant(
            'SERVICE-PROVIDERS-JOIN-NEIGHBORHOODS-PAGE.FAILED_TO_UNAFFILIATE_FROM',
            { neighborhood: neighborhood.name },
          ),
          err,
        );
        this.toastService.showToast(
          this.translate.instant(
            'SERVICE-PROVIDERS-JOIN-NEIGHBORHOODS-PAGE.FAILED_TO_LEAVE',
            { neighborhood: neighborhood.name },
          ),
          'error',
        );
      },
    });
  }

  toggleItem(neighborhoodId: string, name: string, event: MouseEvent): void {
    event.stopPropagation(); 

    if (this.selectedNeighborhoodIds.includes(neighborhoodId)) {
      this.selectedNeighborhoodIds = this.selectedNeighborhoodIds.filter(
        id => id !== neighborhoodId,
      );
    } else {
      this.selectedNeighborhoodIds.push(neighborhoodId);
    }

    this.updateDisplayText();
  }

  get displayText(): string {
    if (this.selectedNeighborhoodIds.length === 0) {
      return this.translate.instant(
        'SERVICE-PROVIDERS-JOIN-NEIGHBORHOODS-PAGE.SELECT_NEIGHBORHOOD',
      );
    }
    const selected = this.otherNeighborhoods
      .filter(o => this.selectedNeighborhoodIds.includes(o.self))
      .map(o => o.name);

    if (selected.length > 1) {
      return `(${selected.length}) ${selected.join(', ')}`;
    } else {
      return selected.join(', ');
    }
  }

  updateDisplayText(): void {
  }

  toggleSelect(): void {
    this.isSelectOpen = !this.isSelectOpen;
  }

  onSubmit(): void {
    this.affiliationService
      .createAffiliations(this.selectedNeighborhoodIds)
      .subscribe({
        next: () => {
          this.toastService.showToast(
            this.translate.instant(
              'SERVICE-PROVIDERS-JOIN-NEIGHBORHOODS-PAGE.REQUEST_ACCEPTED',
            ),
            'success',
          );

          this.loadAssociatedNeighborhoods();

          this.resetScrollState();
          this.loadOtherNeighborhoods(true);

          this.selectedNeighborhoodIds = [];

          this.reinitializeScrollListener();
        },
        error: () => {
          this.toastService.showToast(
            this.translate.instant(
              'SERVICE-PROVIDERS-JOIN-NEIGHBORHOODS-PAGE.REQUEST_FAILED',
            ),
            'error',
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
        return;
      }

      if (!this.isListenerAttached) {
        this.listItemsRef.nativeElement.addEventListener(
          'scroll',
          this.onListScroll.bind(this),
        );
        this.isListenerAttached = true; 
      }
    }, 0); 
  }


  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (
      this.isSelectOpen &&
      this.selectBtnRef &&
      !this.selectBtnRef.nativeElement.contains(event.target)
    ) {
      this.isSelectOpen = false;
    }
  }
}
