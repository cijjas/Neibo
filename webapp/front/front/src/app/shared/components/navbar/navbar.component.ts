import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserSessionService } from '@core/index';
import { PreferencesService } from '@core/index';
import { UserService } from '@shared/index';
import { User } from '@shared/models';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  neighborhoodName: string;
  isDarkMode: boolean = false;
  private currentUser: User | null = null;

  constructor(
    private userSessionService: UserSessionService,
    private router: Router,
    private preferencesService: PreferencesService,
    private userService: UserService
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

    this.userSessionService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.isDarkMode = user?.darkMode || false;
        this.preferencesService.applyDarkMode(this.isDarkMode);
      },
      error: () => {
        console.error('Error fetching user information');
      },
    });
  }

  routeBasedOnRole() {
    this.router.navigate(['']);
  }

  onDarkModeToggle(): void {
    if (!this.currentUser) {
      console.error('No user is currently logged in.');
      return;
    }

    // Toggle the local dark mode state
    const newDarkMode = !this.isDarkMode;
    this.preferencesService.applyDarkMode(newDarkMode);

    // Update the dark mode preference in the backend
    this.userService.toggleDarkMode(this.currentUser).subscribe({
      next: (updatedUser) => {
        if (updatedUser) {
          // Update the user session with the new preference
          this.userSessionService.setUserInformation(updatedUser);
          this.isDarkMode = updatedUser.darkMode;
        }
      },
      error: (error) => {
        console.error('Failed to toggle dark mode:', error);
        // Revert the theme change if the update fails
        this.preferencesService.applyDarkMode(this.isDarkMode);
      },
    });
  }
}
