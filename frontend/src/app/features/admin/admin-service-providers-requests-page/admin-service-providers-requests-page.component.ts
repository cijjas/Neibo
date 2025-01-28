import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import {
  ConfirmationService,
  HateoasLinksService,
  ToastService,
} from '@core/index';
import {
  WorkerService,
  Worker,
  AffiliationService,
  LinkKey,
} from '@shared/index';

@Component({
  selector: 'app-admin-service-providers-requests-page',
  templateUrl: './admin-service-providers-requests-page.component.html',
})
export class AdminServiceProvidersRequestsPageComponent implements OnInit {
  serviceProviders: boolean = false; // Indicates if working with service providers
  workers: Worker[] = [];
  totalPages: number = 1;
  currentPage: number = 1;
  pageSize: number = 10;

  constructor(
    private workerService: WorkerService,
    private linkService: HateoasLinksService,
    private toastService: ToastService,
    private affiliationService: AffiliationService,
    private route: ActivatedRoute,
    private confirmationService: ConfirmationService,
    private router: Router,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.route.url.subscribe((urlSegments) => {
      const currentRoute = urlSegments.map((segment) => segment.path).join('/');
      this.route.queryParams.subscribe((params) => {
        this.currentPage = +params['page'] || 1; // Default to page 1
        this.pageSize = +params['size'] || 10; // Default to size 10
        this.loadWorkers(currentRoute);
      });
    });
  }

  loadWorkers(currentRoute: string): void {
    const neighborhoodUrl: string = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_SELF
    );
    let roleUrl: string;

    if (currentRoute === 'service-providers/requests') {
      roleUrl = this.linkService.getLink(LinkKey.UNVERIFIED_WORKER_ROLE);
    } else if (currentRoute === 'service-providers') {
      this.serviceProviders = true;
      roleUrl = this.linkService.getLink(LinkKey.VERIFIED_WORKER_ROLE);
    } else {
      return;
    }

    this.workerService
      .getWorkers({
        inNeighborhood: [neighborhoodUrl],
        withRole: roleUrl,
        page: this.currentPage,
        size: this.pageSize,
      })
      .subscribe({
        next: (res) => {
          this.workers = res.workers;
          this.totalPages = res.totalPages || 1;
        },
        error: (err) => {
          console.error(this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.ERROR_LOADING_WORKERS'), err);
          this.workers = [];
        },
      });
  }

  rejectWorker(worker: Worker): void {
    const actionDetails = this.serviceProviders
      ? {
        title: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.REMOVE_SERVICE_PROVIDER'),
        message: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.ARE_YOU_SURE_YOU_WANT_TO_REMOVE_WORKERUSERNAME_AS_', {workerName: worker.user.name}),
        confirmText: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.YES_REMOVE'),
        successMessage: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.WORKERUSERNAME_HAS_BEEN_SUCCESSFULLY_REMOVED_AS_A_', {workerName: worker.user.name}),
        errorMessage: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.WE_ENCOUNTERED_AN_ISSUE_WHILE_TRYING_TO_REMOVE_WOR', {workerName: worker.user.name}),
      }
      : {
        title: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.REJECT_SERVICE_PROVIDER_REQUEST'),
        message: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.ARE_YOU_SURE_YOU_WANT_TO_DECLINE_THE_REQUEST_FROM_', {workerName: worker.user.name}),
        confirmText: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.YES_REJECT'),
        successMessage: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.THE_REQUEST_FROM_WORKERUSERNAME_HAS_BEEN_SUCCESSFU', {workerName: worker.user.name}),
        errorMessage: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.WE_ENCOUNTERED_AN_ISSUE_WHILE_TRYING_TO_DECLINE_TH', {workerName: worker.user.name}),
      };

    this.confirmationService
      .askForConfirmation({
        title: actionDetails.title,
        message: actionDetails.message,
        confirmText: actionDetails.confirmText,
        cancelText: this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.CANCEL'),
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.affiliationService.rejectWorker(worker.self).subscribe({
            next: () => {
              this.toastService.showToast(
                actionDetails.successMessage,
                'success'
              );
              this.loadWorkers(
                this.route.snapshot.url.map((segment) => segment.path).join('/')
              );
            },
            error: () => {
              this.toastService.showToast(actionDetails.errorMessage, 'error');
            },
          });
        }
      });
  }

  verifyWorker(worker: Worker) {
    this.affiliationService.verifyWorker(worker.self).subscribe({
      next: () => {
        this.toastService.showToast(
          this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.WORKER_WAS_VERIFIED_SUCCESSFULLY', {workerName: worker.user.name}),
          'success'
        );
        this.loadWorkers(
          this.route.snapshot.url.map((segment) => segment.path).join('/')
        );
      },
      error: () => {
        this.toastService.showToast(
          this.translate.instant('ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.SOMETHING_WENT_WRONG_WORKER_COULD_NOT', {workerName: worker.user.name}),
          'error'
        );
      },
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.updateQueryParams();
    this.loadWorkers(
      this.route.snapshot.url.map((segment) => segment.path).join('/')
    );
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.currentPage, size: this.pageSize },
      queryParamsHandling: 'merge', // Merge with other existing query params
    });
  }
}
