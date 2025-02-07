import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { AuthService } from '@core/index';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';

@Component({
  selector: 'app-unverified-page',
  templateUrl: './unverified-page.component.html',
})
export class UnverifiedPageComponent {
  constructor(
    private authService: AuthService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit() {
    const title = this.translate.instant(AppTitleKeys.UNVERIFIED_PAGE);
    this.titleService.setTitle(title);
  }
  goBackToMainPage(): void {
    this.authService.logout();
  }
}
