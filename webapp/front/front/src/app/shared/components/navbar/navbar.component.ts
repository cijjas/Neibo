import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserSessionService } from '@core/index'; //ThemeService,
import { User } from '@shared/models';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'], // Corrected 'styleUrl' to 'styleUrls'
})
export class NavbarComponent implements OnInit {
  neighborhoodName: string;
  isDarkMode: boolean = false;

  constructor(
    private userSessionService: UserSessionService,
    private router: Router // private themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.userSessionService.getCurrentNeighborhood().subscribe({
      next: (neighborhood) => {
        this.neighborhoodName = neighborhood?.name || 'Neighborhood';
      },
      error: () => {
        console.error('Error fetching neighborhood information');
        this.neighborhoodName = 'Error loading neighborhood';
      },
    });

    // // Subscribe to dark mode changes
    // this.themeService.darkMode$.subscribe((isDark) => {
    //   this.isDarkMode = isDark;
    // });
  }

  routeBasedOnRole() {
    this.router.navigate(['']);
  }

  onDarkModeToggle(): void {
    // this.themeService.toggleDarkMode();
  }
}
