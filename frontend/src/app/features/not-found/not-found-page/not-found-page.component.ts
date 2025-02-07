import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';

@Component({
  selector: 'app-not-found-page',
  templateUrl: './not-found-page.component.html',
})
export class NotFoundPageComponent implements OnInit {
  errorCode: string = '404';
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    const title = this.translate.instant(AppTitleKeys.NOT_FOUND_PAGE);
    this.titleService.setTitle(title);

    this.route.queryParams.subscribe(params => {
      if (params['code']) {
        this.errorCode = params['code'];
      }
      if (params['message']) {
        this.errorMessage = params['message'];
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
