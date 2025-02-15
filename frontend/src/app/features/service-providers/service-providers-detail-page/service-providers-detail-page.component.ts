import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  ToastService,
  HateoasLinksService,
  UserSessionService,
} from '@core/index';
import {
  WorkerService,
  ReviewService,
  User,
  Worker,
  WorkerDto,
  LinkKey,
} from '@shared/index';
import { ServiceProvidersReviewsAndPostsComponent } from '../service-providers-reviews-and-posts/service-providers-reviews-and-posts.component';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-service-providers-detail-page',
  templateUrl: './service-providers-detail-page.component.html',
})
export class ServiceProvidersDetailPageComponent implements OnInit {
  worker: Worker | null = null;
  reviewDialogVisible = false;
  editDialogVisible = false;
  @ViewChild(ServiceProvidersReviewsAndPostsComponent)
  tabbedBox!: ServiceProvidersReviewsAndPostsComponent;

  constructor(
    private workerService: WorkerService,
    private route: ActivatedRoute,
    private reviewService: ReviewService,
    private toastService: ToastService,
    private linkService: HateoasLinksService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(({ worker }) => {
      if (!worker) {
        console.error('Service Provider not found or failed to resolve');
      }
      this.worker = worker;

      const title = this.translate.instant(
        AppTitleKeys.SERVICE_PROVIDERS_DETAIL_PAGE,
        { businessName: this.worker.businessName },
      );
      this.titleService.setTitle(title);
    });
  }

  loadWorker(id: string): void {
    this.workerService.getWorker(id).subscribe({
      next: worker => {
        this.worker = worker;
      },
      error: err => {
        console.error('Error loading worker:', err);
      },
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
      user: this.linkService.getLink(LinkKey.USER_SELF),
    };

    this.reviewService.createReview(this.worker?.reviews, newReview).subscribe({
      next: () => {
        this.toastService.showToast(
          this.translate.instant(
            'SERVICE-PROVIDERS-DETAIL-PAGE.REVIEW_SUBMITTED_SUCCESSFULLY',
          ),
          'success',
        );

        if (this.tabbedBox) {
          this.tabbedBox.reloadReviews();
        }
        if (this.worker?.self) {
          this.loadWorker(this.worker.self);
        }
      },
      error: err => {
        if (
          err.status === 403 &&
          err.error?.message?.includes(
            "You don't have enough permissions to perform this action",
          )
        ) {
          this.toastService.showToast(
            this.translate.instant(
              'SERVICE-PROVIDERS-DETAIL-PAGE.ONLY_ONE_REVIEW_PER_DAY',
            ),
            'error',
          );
        } else {
          this.toastService.showToast(
            this.translate.instant(
              'SERVICE-PROVIDERS-DETAIL-PAGE.FAILED_TO_SUBMIT_REVIEW',
            ),
            'error',
          );
        }
      },
    });
  }

  onSaveProfile(workerDto: WorkerDto): void {
    this.workerService.updateWorker(workerDto).subscribe({
      next: () => {
        this.toastService.showToast(
          this.translate.instant(
            'SERVICE-PROVIDERS-DETAIL-PAGE.NEW_PROFILE_INFORMATION_SAVED_SUCCESFULLY',
          ),
          'success',
        );
        if (this.worker?.self) {
          this.loadWorker(this.worker.self);
        }
      },
      error: err => {
        this.toastService.showToast(
          this.translate.instant(
            'SERVICE-PROVIDERS-DETAIL-PAGE.THERE_WAS_AN_ERROR_UPDATING_PROFILE_TRY_AGAIN',
          ),
          'error',
        );
        console.error('Error saving profile:', err);
      },
    });
  }

  isTheWorker() {
    return this.linkService.getLink(LinkKey.USER_WORKER) === this.worker?.self;
  }
}
