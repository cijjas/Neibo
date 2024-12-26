import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'addHours'
})
export class AddHoursPipe implements PipeTransform {
  transform(date: string | Date, hours: number): string {
    const newDate = new Date(date);
    newDate.setHours(newDate.getHours() + hours);
    return newDate.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });
  }
}
