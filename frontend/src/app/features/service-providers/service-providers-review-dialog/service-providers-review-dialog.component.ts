import { Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';

@Component({
  selector: 'app-service-providers-review-dialog',
  templateUrl: './service-providers-review-dialog.component.html',
})
export class ServiceProvidersReviewDialogComponent implements OnDestroy {
  private workerSubject = new BehaviorSubject<any>(null);

  @Input()
  set worker(value: any) {
    this.workerSubject.next(value);
  }
  get worker(): any {
    return this.workerSubject.value;
  }

  @Input() reviewDialogVisible = false;
  @Output() closeDialog = new EventEmitter<void>();
  @Output() submitReview = new EventEmitter<{ rating: number; message: string }>();

  stars = [1, 2, 3, 4, 5];
  rating = 0;
  reviewMessage = '';

  closeReviewDialog(): void {
    this.closeDialog.emit();
  }

  setRating(value: number): void {
    this.rating = value;
  }

  submitReviewForm(): void {
    if (this.reviewMessage.trim().length >= 10 && this.rating > 0) {
      this.submitReview.emit({
        rating: this.rating,
        message: this.reviewMessage,
      });
      this.closeReviewDialog();
    }
  }

  ngOnDestroy(): void {
    this.workerSubject.complete();
  }
}