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
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
})
export class MainLayoutComponent implements OnInit {
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
    this.userSessionService.getCurrentUser().subscribe({
      next: user => {
        this.currentUser = user;

        // Initialize preferences
        this.isDarkMode = user?.darkMode || false;
        this.currentLanguage = user?.language || this.englishLanguageLink;

        // Apply preferences
        this.preferencesService.applyDarkMode(this.isDarkMode);
        this.preferencesService.applyLanguage(this.currentLanguage);

        // Apply translation based on the current language
        this.translate.use(
          this.currentLanguage === this.englishLanguageLink ? 'en' : 'es',
        );
      },
      error: () => {
        this.isDarkMode = false;
        this.translate.setDefaultLang('en');
        this.translate.use('en');
        console.error('Error fetching user information');
      },
    });
  }
}
