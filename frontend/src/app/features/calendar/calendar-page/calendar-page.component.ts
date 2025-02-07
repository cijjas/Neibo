import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';

@Component({
  selector: 'app-calendar-page',
  templateUrl: './calendar-page.component.html',
})
export class CalendarPageComponent implements OnInit {
  selectedDate: Date;
  formattedSelectedDate: string;

  constructor(
    private route: ActivatedRoute,
    private titleService: Title,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    const title = this.translate.instant(AppTitleKeys.CALENDAR_PAGE);
    this.titleService.setTitle(title);

    this.route.queryParams.subscribe(params => {
      const dateParam = params['date'];
      if (dateParam) {
        const [y, m, d] = dateParam.split('-').map(Number);
        // Create the date at noon local time to avoid offsets
        this.selectedDate = new Date(y, m - 1, d, 12);
      } else {
        const now = new Date();
        this.selectedDate = new Date(
          now.getFullYear(),
          now.getMonth(),
          now.getDate(),
          12,
        );
      }
      this.updateFormattedDate();
    });
  }

  updateFormattedDate(): void {
    const options: Intl.DateTimeFormatOptions = {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    };
    this.formattedSelectedDate = this.selectedDate.toLocaleDateString(
      'en-US',
      options,
    );
  }
}
