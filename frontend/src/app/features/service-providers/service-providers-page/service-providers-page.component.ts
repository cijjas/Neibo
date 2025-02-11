import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService, UserSessionService } from '@core/index';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { LinkKey, Role, Worker, WorkerService } from '@shared/index';

@Component({
  selector: 'app-service-providers-page',
  templateUrl: './service-providers-page.component.html',
})
export class ServiceProvidersPageComponent implements OnInit {
  workersList: Worker[] = [];
  currentPage: number = 1;
  totalPages: number = 0;
  pageSize: number = 10;
  professions: string[] = [];

  isLoading: boolean = true;
  placeholderItems = Array.from({ length: 10 }, (_, i) => i);

  constructor(
    private workerService: WorkerService,
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private titleService: Title,
    private linkService: HateoasLinksService,
    private userSessionService: UserSessionService,
  ) {}

  ngOnInit(): void {
    const title = this.translate.instant(AppTitleKeys.SERVICE_PROVIDERS_PAGE);
    this.titleService.setTitle(title);

    this.route.queryParams.subscribe(params => {
      this.currentPage = +params['page'] || 1;
      this.pageSize = +params['size'] || 10;

      const professionsParam = params['withProfession'];
      this.professions = Array.isArray(professionsParam)
        ? professionsParam
        : professionsParam
        ? [professionsParam]
        : [];

      this.loadWorkers();
    });
  }

  loadWorkers() {
    this.isLoading = true;

    const currentUser = this.userSessionService.getCurrentUserValue();
    const isWorker = currentUser.userRole === Role.WORKER;

    const neigh = isWorker
      ? null
      : this.linkService.getLink(LinkKey.NEIGHBORHOOD_SELF);

    const queryParams: any = {
      page: this.currentPage,
      size: this.pageSize,
      withProfession: this.professions,
      withRole: this.linkService.getLink(LinkKey.VERIFIED_WORKER_ROLE),
    };

    if (neigh) {
      queryParams.inNeighborhood = [neigh];
    }

    this.workerService.getWorkers(queryParams).subscribe({
      next: response => {
        if (response) {
          this.workersList = response.workers;
          this.totalPages = response.totalPages;
          this.currentPage = response.currentPage;
        } else {
          this.workersList = [];
          this.totalPages = 0;
        }
        this.isLoading = false;
      },
      error: error => {
        console.error('Error loading workers:', error);
        this.workersList = [];
        this.totalPages = 0;
        this.isLoading = false;
      },
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.updateQueryParams();
    this.loadWorkers();
  }

  onProfessionChange(selectedProfessions: string[]): void {
    this.professions = selectedProfessions;
    this.updateQueryParams();
    this.loadWorkers();
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        page: this.currentPage,
        size: this.pageSize,
        withProfession: this.professions,
      },
      queryParamsHandling: 'merge',
    });
  }
}
