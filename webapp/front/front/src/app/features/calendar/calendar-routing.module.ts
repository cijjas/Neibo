import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CalendarPageComponent, CalendarEventPageComponent } from '@features/index';

const routes: Routes = [
    { path: '', component: CalendarPageComponent },
    { path: 'events/:id', component: CalendarEventPageComponent },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class CalendarRoutingModule { }
