import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Profession, Worker } from '@shared/index';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-service-providers-preview',
  templateUrl: './service-providers-preview.component.html',
})
export class ServiceProvidersPreviewComponent implements OnInit {
  @Input() worker: Worker;

  profileImageUrl: string = '';
  backgroundImageUrl: string = '';

  constructor(private router: Router, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.profileImageUrl = this.worker?.user?.image
      ? this.worker.user.image
      : environment.deployUrl + 'assets/images/default-profile.png';

    this.backgroundImageUrl = this.worker?.backgroundImage
      ? this.worker.backgroundImage
      : environment.deployUrl + 'assets/images/default-background.png';
  }

  onProfessionClick(event: MouseEvent, profession: Profession) {
    // Prevent the click from reaching the parent <a> tag
    event.stopPropagation();

    // Now call your filtering function
    this.setProfession(profession);
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
