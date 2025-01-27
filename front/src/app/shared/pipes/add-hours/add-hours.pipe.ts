import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'addHours',
  standalone: true
})
export class AddHoursPipe implements PipeTransform {

  transform(value: unknown, ...args: unknown[]): unknown {
    return null;
  }

}
