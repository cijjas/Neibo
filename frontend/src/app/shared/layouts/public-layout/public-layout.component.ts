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

  constructor(
    private userSessionService: UserSessionService,
    private preferencesService: PreferencesService,
    private linkService: HateoasLinksService,
    private translate: TranslateService,
  ) {
    this.englishLanguageLink = this.linkService.getLink(
      LinkKey.ENGLISH_LANGUAGE,
    );
    this.spanishLanguageLink = this.linkService.getLink(
      LinkKey.SPANISH_LANGUAGE,
    );

    this.translate.setDefaultLang('en');
  }

  ngOnInit(): void {
    this.userSessionService.getCurrentUser().subscribe({
      next: user => {
        this.isDarkMode = user?.darkMode || false;
        const userLanguageLink = user?.language || this.englishLanguageLink;

        if (userLanguageLink === this.preferencesService.languageMap['en']) {
          this.currentLanguage = 'en';
        } else if (
          userLanguageLink === this.preferencesService.languageMap['es']
        ) {
          this.currentLanguage = 'es';
        } else {
          this.currentLanguage = 'en';
        }

        this.preferencesService.applyDarkMode(this.isDarkMode);
        this.preferencesService.applyLanguage(this.currentLanguage);

        if (this.translate.currentLang !== this.currentLanguage) {
          this.translate.use(this.currentLanguage);
        }
      },
      error: () => {
        this.isDarkMode = false;
        this.preferencesService.applyLanguage('en');
        this.translate.use('en');
        console.error('Error fetching user information');
      },
    });
  }
}
