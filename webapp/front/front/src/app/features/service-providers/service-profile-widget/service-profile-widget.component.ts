import { Component, Input, OnInit } from '@angular/core';
import { Worker } from '@shared/index';

@Component({
  selector: 'app-service-profile-widget',
  templateUrl: './service-profile-widget.component.html',
})
export class ServiceProfileWidgetComponent implements OnInit {
  @Input() worker: Worker;

  profileImageUrl: string = '';
  backgroundImageUrl: string = '';

  ngOnInit(): void {
    this.profileImageUrl = this.worker?.user?.image
      ? this.worker.user.image
      : 'assets/images/default-profile.png';

    this.backgroundImageUrl = this.worker?.backgroundImage
      ? this.worker.backgroundImage
      : 'assets/images/default-background.png';
  }
}
