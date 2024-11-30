// ./app/components/navbar/navbar.component.ts
import {Component, OnInit} from '@angular/core';
import {NeighborhoodService} from '../../shared/services/index.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
})
export class NavbarComponent{
  
  neighborhoodName: string = 'Neighborhood';

  constructor(
    private neighborhoodService: NeighborhoodService,
  ) { }


  ngOnInit() {
    

    
  }


}
