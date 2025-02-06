import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Worker, Profession } from '@shared/index';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-service-providers-content',
  templateUrl: './service-providers-content.component.html',
})
export class ServiceProvidersContentComponent {
  @Input() worker: Worker | null = null; 
  @Input() averageRating: number = 0; 
  @Input() reviewsCount: number = 0; 
  @Input() postCount: number = 0; 
  @Input() isTheWorker: boolean = false; 

  @Output() openEditProfile = new EventEmitter<void>(); 

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
