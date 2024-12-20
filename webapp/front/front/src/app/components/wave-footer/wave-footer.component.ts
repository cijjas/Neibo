import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-wave-footer',
  templateUrl: './wave-footer.component.html',
  styleUrls: ['../../app.component.css']
})
export class WaveFooterComponent {

  @Input() isMarketplace = false;


}
