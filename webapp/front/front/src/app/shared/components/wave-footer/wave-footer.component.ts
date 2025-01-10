import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-wave-footer',
  templateUrl: './wave-footer.component.html',
  styleUrl: './wave-footer.component.scss',
})
export class WaveFooterComponent {
  @Input() theme: 'default' | 'marketplace' | 'services' | 'admin' = 'default';

  constructor() {}

  ngOnInit(): void {}
}
