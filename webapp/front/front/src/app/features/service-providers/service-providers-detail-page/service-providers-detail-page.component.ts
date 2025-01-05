import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ToastService, HateoasLinksService, UserSessionService } from '@core/index';
import { WorkerService, ReviewService, User, Worker, WorkerDto } from '@shared/index';
import { ServiceProvidersReviewsAndPostsComponent } from '@features/index';

@Component({
  selector: 'app-service-providers-detail-page',
  templateUrl: './service-providers-detail-page.component.html',
})
export class ServiceProvidersDetailPageComponent implements OnInit {
  darkMode = false;
  worker: Worker | null = null;
  reviewDialogVisible = false;
  editDialogVisible = false;
  @ViewChild(ServiceProvidersReviewsAndPostsComponent) tabbedBox!: ServiceProvidersReviewsAndPostsComponent;


  constructor(
    private workerService: WorkerService,
    private route: ActivatedRoute,
    private reviewService: ReviewService,
    private toastService: ToastService,
    private linkService: HateoasLinksService,
    private userSessionService: UserSessionService,
  ) { }

  ngOnInit(): void {
    const workerId = this.route.snapshot.paramMap.get('id');
    if (workerId) this.loadWorker(workerId);


  }

  loadWorker(id: string): void {
    this.workerService.getWorker(id).subscribe({
      next: (worker) => {
        this.worker = worker;
      },
      error: (err) => console.error('Error loading worker:', err),
    });
  }

  openReviewDialog(): void {
    this.reviewDialogVisible = true;
  }

  closeReviewDialog(): void {
    this.reviewDialogVisible = false;
  }

  openEditDialog(): void {
    this.editDialogVisible = true;
  }

  closeEditDialog(): void {
    this.editDialogVisible = false;
  }

  onSubmitReview(review: { rating: number; message: string }): void {
    this.reviewDialogVisible = false;
    const newReview = {
      ...review,
      user: this.linkService.getLink('user:self')
    };

    // Let's assume this.worker exists
    this.reviewService.createReview(this.worker?.reviews, newReview).subscribe({
      next: () => {
        this.toastService.showToast('Review submitted successfully!', 'success');

        // 2) Directly call child's reload method
        if (this.tabbedBox) {
          this.tabbedBox.reloadReviews();
        }
      },
      error: () => {
        this.toastService.showToast('Failed to submit review.', 'error');
      },
    });
  }

  onSaveProfile(worker: WorkerDto): void {
    this.workerService.updateWorker(worker).subscribe({
      next: () => {
        this.toastService.showToast('New profile information saved successfully!', 'success');
      },
      error: (err) => {
        this.toastService.showToast('There was an error updating profile. Try again.', 'error');
        console.error('Error saving profile:', err);
      },
    });
  }


}
