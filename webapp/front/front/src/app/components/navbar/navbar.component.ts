import { Component, OnInit } from '@angular/core';
import { NeighborhoodService, UserSessionService } from '../../shared/services/index.service';
import { Neighborhood } from '../../shared/models/index';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
})
export class NavbarComponent implements OnInit {

  neighborhoodName: string;

  constructor(
    private userSessionService: UserSessionService
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
}
