import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
})
export class CalendarComponent implements OnInit {
  selectedDate: Date;
  formattedSelectedDate: string;

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const timestamp = params['timestamp'];
      if (timestamp) {
        this.selectedDate = new Date(Number(timestamp));
      } else {
        this.selectedDate = new Date();
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
    this.formattedSelectedDate = this.selectedDate.toLocaleDateString('en-US', options);
  }

}
