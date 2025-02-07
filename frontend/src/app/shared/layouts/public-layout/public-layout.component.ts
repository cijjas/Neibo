import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {
  HateoasLinksService,
  PreferencesService,
  UserSessionService,
} from '@core/index';
import { TranslateService } from '@ngx-translate/core';
import { LinkKey, User } from '@shared/models';
import { UserService } from '@shared/services/domain/user.service';

@Component({
  selector: 'app-public-layout',
  templateUrl: './public-layout.component.html',
})
export class PublicLayoutComponent implements OnInit {
  neighborhoodName: string;
  isDarkMode: boolean = false;
  currentLanguage: string = '';
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
    // Get language links using the link service
    this.englishLanguageLink = this.linkService.getLink(
      LinkKey.ENGLISH_LANGUAGE,
    );
    this.spanishLanguageLink = this.linkService.getLink(
      LinkKey.SPANISH_LANGUAGE,
    );

    // Set a default language for TranslateService (fallback)
    this.translate.setDefaultLang('en');
  }

  ngOnInit(): void {
    this.userSessionService.getCurrentUser().subscribe({
      next: user => {
        this.currentUser = user;

        // Initialize preferences from user info
        this.isDarkMode = user?.darkMode || false;
        // Assume user.language holds the API language link (e.g., "http://.../languages/1")
        const userLanguageLink = user?.language || this.englishLanguageLink;

        // Use the languageMap to convert the API language link into a language code.
        if (userLanguageLink === this.preferencesService.languageMap['en']) {
          this.currentLanguage = 'en';
        } else if (
          userLanguageLink === this.preferencesService.languageMap['es']
        ) {
          this.currentLanguage = 'es';
        } else {
          // Fallback if the link does not match; default to English.
          this.currentLanguage = 'en';
        }

        // Apply and store preferences
        this.preferencesService.applyDarkMode(this.isDarkMode);
        this.preferencesService.applyLanguage(this.currentLanguage);

        // Update TranslateService with the determined language
        if (this.translate.currentLang !== this.currentLanguage) {
          this.translate.use(this.currentLanguage);
        }
      },
      error: () => {
        // Fallback if user information cannot be fetched
        this.isDarkMode = false;
        this.preferencesService.applyLanguage('en');
        this.translate.use('en');
        console.error('Error fetching user information');
      },
    });
  }
}
