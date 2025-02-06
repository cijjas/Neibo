import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CalendarPageComponent } from './calendar-page/calendar-page.component';
import { CalendarEventPageComponent } from './calendar-event-page/calendar-event-page.component';

import { eventResolver } from '@shared/resolvers/event.resolver';

const routes: Routes = [
  { path: '', component: CalendarPageComponent },
  {
    path: 'events/:id',
    component: CalendarEventPageComponent,
    resolve: { event: eventResolver },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CalendarRoutingModule {}
