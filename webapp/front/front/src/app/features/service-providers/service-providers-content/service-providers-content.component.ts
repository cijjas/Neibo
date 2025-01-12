import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Worker, Profession } from '@shared/index';
import { Router, ActivatedRoute } from '@angular/router';
import { HateoasLinksService } from '@core/index';

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

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private linkService: HateoasLinksService
  ) {}

  get backgroundImageUrl(): string {
    return (
      this.worker?.backgroundImage || 'assets/images/default-background.png'
    );
  }

  get profileImageUrl(): string {
    return this.worker?.user?.image || 'assets/images/default-profile.png';
  }

  setProfession(prof: Profession | null): void {
    if (prof === null) {
      this.router.navigate(['/services'], {
        relativeTo: this.route,
        queryParams: { professions: null },
      });
    } else {
      this.router.navigate(['/services'], {
        relativeTo: this.route,
        queryParams: { professions: prof.self },
      });
    }
  }
}
