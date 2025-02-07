import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
})
export class LoginPageComponent {
  showLoginDialog: boolean = false;
  showSignupDialog: boolean = false;

  constructor(
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit() {
    const title = this.translate.instant(AppTitleKeys.LOGIN_PAGE);
    this.titleService.setTitle(title);
  }
  openLoginDialog(): void {
    this.showLoginDialog = true;
  }

  openSignupDialog(): void {
    this.showSignupDialog = true;
  }

  closeLoginDialog(): void {
    this.showLoginDialog = false;
  }

  closeSignupDialog(): void {
    this.showSignupDialog = false;
  }
}
