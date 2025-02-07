import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { AuthService } from '@core/index';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';

@Component({
  selector: 'app-super-admin-page',
  templateUrl: './super-admin-page.component.html',
})
export class SuperAdminPageComponent {
  constructor(
    private authService: AuthService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit() {
    const title = this.translate.instant(AppTitleKeys.SUPER_ADMIN_PAGE);
    this.titleService.setTitle(title);
  }

  goBackToMainPage(): void {
    this.authService.logout();
  }
}
