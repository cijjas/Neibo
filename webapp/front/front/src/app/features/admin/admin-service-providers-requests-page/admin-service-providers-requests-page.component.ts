import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService, ToastService } from '@core/index';
import { WorkerService, Worker, AffiliationService } from '@shared/index';

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
    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.url.subscribe((urlSegments) => {
      const currentRoute = urlSegments.map(segment => segment.path).join('/');
      this.route.queryParams.subscribe((params) => {
        this.currentPage = +params['page'] || 1; // Default to page 1
        this.pageSize = +params['size'] || 10; // Default to size 10
        this.loadWorkers(currentRoute);
      });
    });
  }

  loadWorkers(currentRoute: string): void {
    const neighborhoodUrl: string = this.linkService.getLink('neighborhood:self');
    let roleUrl: string;

    if (currentRoute === 'service-providers/requests') {
      roleUrl = this.linkService.getLink('neighborhood:unverifiedWorkerRole');
    } else if (currentRoute === 'service-providers') {
      this.serviceProviders = true;
      roleUrl = this.linkService.getLink('neighborhood:verifiedWorkerRole');
    } else {
      return;
    }

    this.workerService.getWorkers({ inNeighborhoods: [neighborhoodUrl], withRole: roleUrl, page: this.currentPage, size: this.pageSize })
      .subscribe({
        next: (res) => {
          this.workers = res.workers;
          this.totalPages = res.totalPages || 1;
        },
        error: (err) => {
          console.error('Error loading workers:', err);
          this.workers = [];
        }
      });
  }

  rejectWorker(worker: Worker) {
    this.affiliationService.rejectWorker(worker.self).subscribe({
      next: () => {
        this.toastService.showToast('Worker ' + worker.user.name + ' rejected successfully.', 'success');
        this.loadWorkers(this.route.snapshot.url.map(segment => segment.path).join('/'));
      },
      error: () => {
        this.toastService.showToast('Something went wrong, worker ' + worker.user.name + ' could not be rejected.', 'error');
      }
    });
  }

  verifyWorker(worker: Worker) {
    this.affiliationService.verifyWorker(worker.self).subscribe({
      next: () => {
        this.toastService.showToast('Worker ' + worker.user.name + ' was verified successfully.', 'success');
        this.loadWorkers(this.route.snapshot.url.map(segment => segment.path).join('/'));
      },
      error: () => {
        this.toastService.showToast('Something went wrong, worker ' + worker.user.name + ' could not be verified.', 'error');
      }
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.updateQueryParams();
    this.loadWorkers(this.route.snapshot.url.map(segment => segment.path).join('/'));
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.currentPage, size: this.pageSize },
      queryParamsHandling: 'merge', // Merge with other existing query params
    });
  }
}
