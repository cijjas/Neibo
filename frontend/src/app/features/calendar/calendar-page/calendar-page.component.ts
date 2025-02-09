import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { format, formatDate } from 'date-fns';
import { enUS, es } from 'date-fns/locale';

@Component({
  selector: 'app-calendar-page',
  templateUrl: './calendar-page.component.html',
})
export class CalendarPageComponent implements OnInit {
  selectedDate: Date;

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

  formattedNumericDayMonth: string = '';
  formattedYear: string = '';
  formattedSelectedDay: string = '';

  updateFormattedDate(): void {
    const language = localStorage.getItem('language');
    const locale = language === 'en' ? enUS : es;

    let dayAndMonth = format(this.selectedDate, 'd MMMM', { locale });
    const parts = dayAndMonth.split(' ');
    if (parts.length >= 2) {
      parts[1] = parts[1].charAt(0).toUpperCase() + parts[1].slice(1);
    }
    this.formattedNumericDayMonth = parts.join(' ');

    this.formattedYear = format(this.selectedDate, 'yyyy', { locale });

    let dayOfWeek = format(this.selectedDate, 'EEEE', { locale });
    this.formattedSelectedDay =
      dayOfWeek.charAt(0).toUpperCase() + dayOfWeek.slice(1);
  }
}
