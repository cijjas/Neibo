import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Worker, WorkerService } from '@shared/index';
import { switchMap } from 'rxjs';
import { of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Component({
  selector: 'app-service-providers-page',
  templateUrl: './service-providers-page.component.html',
})
export class ServiceProvidersPageComponent implements OnInit {
  workersList: Worker[] = [];
  currentPage: number = 1;
  totalPages: number = 0;
  pageSize: number = 10;
  professions: string[] = []; // Changed to handle `withProfession` query params
  loading: boolean = true;

  constructor(
    private workerService: WorkerService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.queryParams
      .pipe(
        switchMap((params) => {
          // Handle `page` and `size` query params
          this.currentPage = +params['page'] || 1;
          this.pageSize = +params['size'] || 10;

          // Handle multiple `withProfession` params
          const professionsParam = params['withProfession'];
          this.professions = Array.isArray(professionsParam)
            ? professionsParam
            : professionsParam
              ? [professionsParam]
              : [];

          return this.loadWorkers();
        })
      )
      .subscribe();
  }

  loadWorkers() {
    const queryParams = {
      page: this.currentPage,
      size: this.pageSize,
      withProfession: this.professions,
    };
    console.log(this.professions)
    return this.workerService.getWorkers(queryParams).pipe(
      map((response) => {
        if (response) {
          this.workersList = response.workers;
          this.totalPages = response.totalPages;
          this.currentPage = response.currentPage;
        } else {
          this.workersList = [];
          this.totalPages = 0;
        }
        this.loading = false;
      }),
      catchError((error) => {
        console.error('Error loading workers:', error);
        this.workersList = [];
        this.totalPages = 0;
        this.loading = false;
        return of();
      })
    );
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.updateQueryParams();
    this.loadWorkers().subscribe();
  }

  onProfessionChange(selectedProfessions: string[]): void {
    this.professions = selectedProfessions;
    this.updateQueryParams();
    this.loadWorkers().subscribe();
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        page: this.currentPage,
        size: this.pageSize,
        withProfession: this.professions, // Update query params for professions
      },
      queryParamsHandling: 'merge',
    });
  }
}
