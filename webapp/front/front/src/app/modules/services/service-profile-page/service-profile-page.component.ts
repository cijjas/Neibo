import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { User, Worker } from '../../../shared/models';
import { WorkerService, ToastService, ReviewService, HateoasLinksService, UserSessionService } from '../../../shared/services/index.service';
import { TabbedBoxComponent } from '../../../components/tabbed-box/tabbed-box.component';

@Component({
  selector: 'app-service-profile-page',
  templateUrl: './service-profile-page.component.html',
})
export class ServiceProfilePageComponent implements OnInit {
  darkMode = false;
  worker: Worker | null = null;
  loggedUser: User = null;
  reviewDialogVisible = false;
  editDialogVisible = false;
  @ViewChild(TabbedBoxComponent) tabbedBox!: TabbedBoxComponent;


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
    this.userSessionService.getCurrentUser().subscribe(
      (user) => this.loggedUser = user
    )

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

  onSaveProfile(worker: Worker): void {
    // this.workerService.updateWorker(worker).subscribe({
    //   next: () => {
    //     this.worker = worker;
    //     this.showEditDialog = false;
    //   },
    //   error: (err) => console.error('Error saving profile:', err),
    // });
  }


}
