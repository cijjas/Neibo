import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
    private router: Router
  ) {}

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
        inNeighborhoods: [neighborhoodUrl],
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
          console.error('Error loading workers:', err);
          this.workers = [];
        },
      });
  }

  rejectWorker(worker: Worker): void {
    const actionDetails = this.serviceProviders
      ? {
          title: 'Remove Service Provider',
          message: `Are you sure you want to remove "${worker.user.name}" as a service provider? This action cannot be undone.`,
          confirmText: 'Yes, Remove',
          successMessage: `"${worker.user.name}" has been successfully removed as a service provider.`,
          errorMessage: `We encountered an issue while trying to remove "${worker.user.name}" as a service provider. Please check your connection or try again later.`,
        }
      : {
          title: 'Reject Service Provider Request',
          message: `Are you sure you want to decline the request from "${worker.user.name}"? This action cannot be undone.`,
          confirmText: 'Yes, Reject',
          successMessage: `The request from "${worker.user.name}" has been successfully declined.`,
          errorMessage: `We encountered an issue while trying to decline the request from "${worker.user.name}". Please check your connection or try again later.`,
        };

    this.confirmationService
      .askForConfirmation({
        title: actionDetails.title,
        message: actionDetails.message,
        confirmText: actionDetails.confirmText,
        cancelText: 'Cancel',
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
          'Worker ' + worker.user.name + ' was verified successfully.',
          'success'
        );
        this.loadWorkers(
          this.route.snapshot.url.map((segment) => segment.path).join('/')
        );
      },
      error: () => {
        this.toastService.showToast(
          'Something went wrong, worker ' +
            worker.user.name +
            ' could not be verified.',
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
