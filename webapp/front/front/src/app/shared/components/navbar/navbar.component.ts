import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HateoasLinksService, UserSessionService } from '@core/index';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
})
export class NavbarComponent implements OnInit {

  neighborhoodName: string;

  constructor(
    private userSessionService: UserSessionService,
    private router: Router,
    private linkService: HateoasLinksService,
  ) { }

  ngOnInit(): void {
    this.userSessionService.getNeighborhood().subscribe({
      next: (neighborhood) => {
        this.neighborhoodName = neighborhood?.name;
      },
      error: () => {
        console.error('Error fetching neighborhood information');
        this.neighborhoodName = 'Error loading neighborhood';
      }
    });
  }

  routeBasedOnRole() {
    const userRole = this.userSessionService.getCurrentUserRole()
    if (userRole == 'WORKER') {
      const workerId = this.linkService.getLink('user:worker');
      this.router.navigate(['services', 'profile', workerId]);
    }
    else {
      this.router.navigate(['posts']);

    }
  }
}
