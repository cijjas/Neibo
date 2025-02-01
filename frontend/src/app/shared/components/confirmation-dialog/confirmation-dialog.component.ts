import { Component, OnInit } from '@angular/core';
import { ConfirmationService, ConfirmationOptions } from '@core/index';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.css'],
})
export class ConfirmationDialogComponent implements OnInit {
  isVisible = false;
  options: ConfirmationOptions = {
    title: this.translate.instant('CONFIRMATION-DIALOG.TITLE'),
    message: this.translate.instant('CONFIRMATION-DIALOG.MESSAGE'),
    confirmText: this.translate.instant('CONFIRMATION-DIALOG.CONFIRM_TEXT'),
    cancelText: this.translate.instant('CONFIRMATION-DIALOG.CANCEL_TEXT'),
  };

  constructor(
    private confirmationService: ConfirmationService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.confirmationService.onConfirm$().subscribe((options) => {
      this.options = { ...this.options, ...options };
      this.isVisible = true;
    });
  }

  confirm() {
    this.confirmationService.respondToConfirmation(true);
    this.isVisible = false;
  }

  cancel() {
    this.confirmationService.respondToConfirmation(false);
    this.isVisible = false;
  }
}
