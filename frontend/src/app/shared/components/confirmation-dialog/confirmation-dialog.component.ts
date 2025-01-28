import { Component, OnInit } from '@angular/core';
import { ConfirmationService, ConfirmationOptions } from '@core/index';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.css'],
})
export class ConfirmationDialogComponent implements OnInit {
  isVisible = false;
  options: ConfirmationOptions = {
    title: 'Confirm',
    message: 'Are you sure?',
    confirmText: 'Yes',
    cancelText: 'No',
  };

  constructor(private confirmationService: ConfirmationService) {}

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
