import {
  animate,
  query,
  stagger,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { Component, TrackByFunction } from '@angular/core';
import { Toast, ToastService } from '@core/index';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-success-toast',
  templateUrl: './success-toast.component.html',
  styleUrls: ['./success-toast.component.css'],
  animations: [
    // Container-level stagger for the toasts to appear one by one
    trigger('listStagger', [
      transition('* => *', [
        // when new toast
        query(
          ':enter',
          [
            style({ transform: 'translateX(100%)', opacity: 0 }),
            stagger(
              '100ms',
              animate(
                '300ms ease-out',
                style({
                  transform: 'translateX(0)',
                  opacity: 1,
                })
              )
            ),
          ],
          { optional: true }
        ),
        // Animate removed toasts
        query(
          ':leave',
          [
            stagger('100ms', [
              animate(
                '300ms ease-in',
                style({
                  transform: 'translateY(100%)',
                  opacity: 0,
                })
              ),
            ]),
          ],
          { optional: true }
        ),
      ]),
    ]),
  ],
})
export class SuccessToastComponent {
  toasts$: Observable<Toast[]>;
  trackById: TrackByFunction<Toast>;

  constructor(private toastService: ToastService) {
    this.toasts$ = this.toastService.toasts$;
  }

  ngOnInit(): void {}
}
