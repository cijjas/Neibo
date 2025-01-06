import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { LinkKey, Profession, Worker, WorkerService } from '@shared/index';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-service-providers-content',
  templateUrl: './service-providers-content.component.html',
})
export class ServiceProvidersContentComponent {
  worker: Worker | null = null; // No longer an @Input()
  averageRating: number = 0; // Optional: Could be fetched from worker data
  reviewsCount: number = 0; // Optional: Could be fetched from worker data
  postCount: number = 0; // Optional: Could be fetched from worker data
  isTheWorker = false;

  @Output() openEditProfile = new EventEmitter<void>();


  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private linkService: HateoasLinksService,
    private workerService: WorkerService // Inject WorkerService to fetch worker
  ) { }

  ngOnInit(): void {
    const workerId = this.route.snapshot.paramMap.get('id');
    if (workerId) {
      this.fetchWorker(workerId);
    }
  }
  fetchWorker(id: string): void {
    this.workerService.getWorker(id).subscribe({
      next: (worker) => {

        this.worker = worker;
        this.averageRating = worker.averageRating || 0;
        this.reviewsCount = worker.totalReviews;
        this.postCount = worker.totalPosts;

        this.isTheWorker = this.linkService.getLink(LinkKey.USER_WORKER) === this.worker.self;

      },
      error: (err) => {
        console.error('Error fetching worker:', err);
      },
    });
  }


  get backgroundImageUrl(): string {
    if (this.worker && this.worker.backgroundImage) {
      // If your API returns a link, use that
      return this.worker.backgroundImage;
    }
    // fallback
    return 'assets/images/default-background.png';
  }

  get profileImageUrl(): string {
    if (this.worker?.user?.image) {
      return this.worker.user.image; // or however you store it
    }
    // fallback
    return 'assets/images/default-profile.png';
  }

  setProfession(prof: Profession | null): void {
    if (prof === null) {
      this.router.navigate(["/services"], {
        relativeTo: this.route,
        queryParams: { professions: null }
      });
    } else {
      this.router.navigate(["/services"], {
        relativeTo: this.route,
        queryParams: { professions: prof.self },
      });
    }
  }
}
