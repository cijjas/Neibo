import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-wave-footer',
  templateUrl: './wave-footer.component.html',
})
export class WaveFooterComponent {

  @Input() isMarketplace = false;


}
