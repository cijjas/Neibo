import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Worker, Profession } from '@shared/index';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-service-providers-content',
  templateUrl: './service-providers-content.component.html',
})
export class ServiceProvidersContentComponent {
  @Input() worker: Worker | null = null; // Input for worker data
  @Input() averageRating: number = 0; // Input for average rating
  @Input() reviewsCount: number = 0; // Input for reviews count
  @Input() postCount: number = 0; // Input for post count
  @Input() isTheWorker: boolean = false; // Input to indicate if logged user is the worker

  @Output() openEditProfile = new EventEmitter<void>(); // EventEmitter to notify parent

  constructor(private router: Router, private route: ActivatedRoute) {}

  get backgroundImageUrl(): string {
    return (
      this.worker?.backgroundImage ||
      environment.deployUrl + 'assets/images/default-background.png'
    );
  }

  get profileImageUrl(): string {
    return (
      this.worker?.user?.image ||
      environment.deployUrl + 'assets/images/default-profile.png'
    );
  }

  setProfession(prof: Profession | null): void {
    if (prof === null) {
      this.router.navigate(['/services'], {
        relativeTo: this.route,
        queryParams: { withProfession: null },
      });
    } else {
      this.router.navigate(['/services'], {
        relativeTo: this.route,
        queryParams: { withProfession: prof.self },
      });
    }
  }
}
