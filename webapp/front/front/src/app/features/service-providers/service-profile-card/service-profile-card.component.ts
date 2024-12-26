import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Profession, Worker } from '@shared/index';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-service-profile-card',
  templateUrl: './service-profile-card.component.html',
})
export class ServiceProfileCardComponent {
  @Input() worker: Worker | null = null;
  @Input() loggedUser: any = null;
  @Input() averageRating: number = 0;
  @Input() reviewsCount: number = 0;
  @Input() postCount: number = 0;

  @Output() openEditProfile = new EventEmitter<void>();


  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private linkService: HateoasLinksService,
  ) { }

  // Optionally compute a fallback background
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
