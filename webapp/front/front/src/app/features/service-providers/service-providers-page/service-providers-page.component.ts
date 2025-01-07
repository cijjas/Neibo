import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Worker, WorkerService } from '@shared/index';

@Component({
  selector: 'app-service-providers-page',
  templateUrl: './service-providers-page.component.html',
})
export class ServiceProvidersPageComponent implements OnInit {
  workersList: Worker[] = [];
  currentPage: number = 1;
  totalPages: number = 0;
  pageSize: number = 10;
  currentProfessions: string[] = [];

  constructor(
    private workerService: WorkerService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Example of reading page from query parameters
    this.route.queryParams.subscribe((params) => {
      this.currentPage = +params['page'] || 1;
      const professions: string[] = params['professions'];
      this.currentProfessions = professions
        ? Array.isArray(professions)
          ? professions
          : [professions]
        : [];
      this.loadWorkers();
    });
  }

  onPageChange(page: number): void {
    // Handler when user clicks next page in your <app-paginator>
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page },
      queryParamsHandling: 'merge',
    });
  }

  loadWorkers(): void {
    const workerParams = {
      page: this.currentPage,
      size: this.pageSize,
      withProfessions: this.currentProfessions,
    };

    this.workerService.getWorkers(workerParams).subscribe((result) => {
      this.workersList = result.workers;
      this.totalPages = result.totalPages;
      this.currentPage = result.currentPage;
    });
  }
}
