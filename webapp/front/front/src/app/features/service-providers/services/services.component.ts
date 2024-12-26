import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Worker, WorkerService } from '@shared/index';

@Component({
  selector: 'app-services',
  templateUrl: './services.component.html',
})
export class ServicesComponent implements OnInit {
  workersList: Worker[] = [];
  currentPage: number = 1;
  totalPages: number = 0;
  pageSize: number = 10;
  currentProfessions: string[] = [];
  // If you want to respect the "darkMode" class logic
  darkMode: boolean = false; // or load from user session, e.g. userSessionService

  constructor(
    private workerService: WorkerService,
    private route: ActivatedRoute,
    private router: Router,
  ) { }

  ngOnInit(): void {
    // Example of reading page from query parameters
    this.route.queryParams.subscribe(params => {
      this.currentPage = +params['page'] || 1;
      const professions: string[] = params['professions'];
      this.currentProfessions = professions ? (Array.isArray(professions) ? professions : [professions]) : [];
      this.loadWorkers();
    });

    // If you want to load darkMode from user data, do it here:
    // this.userSessionService.getCurrentUser().subscribe(user => {
    //   this.darkMode = user.darkMode;
    // });
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
      withProfessions: this.currentProfessions
    }

    this.workerService.getWorkers(workerParams)
      .subscribe(result => {
        console.log(result)
        this.workersList = result.workers;
        console.log(this.workersList)

        this.totalPages = result.totalPages;
        this.currentPage = result.currentPage;
      });
  }
}
