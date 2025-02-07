import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '@shared/index';
import {
  UserSessionService,
  PreferencesService,
  HateoasLinksService,
} from '@core/index';
import { LinkKey, User } from '@shared/models';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  neighborhoodName: string;
  isDarkMode: boolean = false;
  currentLanguage: string = ''; // Current language link
  englishLanguageLink: string;
  spanishLanguageLink: string;
  private currentUser: User | null = null;

  constructor(
    private userSessionService: UserSessionService,
    private router: Router,
    private preferencesService: PreferencesService,
    private userService: UserService,
    private linkService: HateoasLinksService,
    private translate: TranslateService,
  ) {
    // Precompute language links
    this.englishLanguageLink = this.linkService.getLink(
      LinkKey.ENGLISH_LANGUAGE,
    );
    this.spanishLanguageLink = this.linkService.getLink(
      LinkKey.SPANISH_LANGUAGE,
    );

    // Set default language
    this.translate.setDefaultLang('en');
  }

  ngOnInit(): void {
    this.userSessionService.getCurrentNeighborhood().subscribe({
      next: neighborhood => {
        this.neighborhoodName = neighborhood?.name || 'Neighborhood';
      },
      error: () => {
        console.error('Error fetching neighborhood information');
        this.neighborhoodName = 'Error loading neighborhood';
      },
    });

    this.userSessionService.getCurrentUser().subscribe({
      next: user => {
        this.currentUser = user;
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

    const newDarkMode = !this.isDarkMode;
    this.preferencesService.applyDarkMode(newDarkMode);

    // Update the backend
    this.userService.toggleDarkMode(this.currentUser).subscribe({
      next: updatedUser => {
        if (updatedUser) {
          this.userSessionService.setUserInformation(updatedUser);
          this.isDarkMode = updatedUser.darkMode;
        }
      },
      error: error => {
        console.error('Failed to toggle dark mode:', error);
        this.preferencesService.applyDarkMode(this.isDarkMode); // Revert
      },
    });
  }
}
