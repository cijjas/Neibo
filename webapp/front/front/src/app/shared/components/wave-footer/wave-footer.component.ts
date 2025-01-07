import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-wave-footer',
  templateUrl: './wave-footer.component.html',
  styleUrl: './wave-footer.component.css',
})
export class WaveFooterComponent {
  @Input() isMarketplace = false;
}
