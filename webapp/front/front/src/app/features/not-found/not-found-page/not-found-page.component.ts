import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-not-found-page',
  templateUrl: './not-found-page.component.html',
})
export class NotFoundPageComponent implements OnInit {
  errorCode: string = '404'; // Default code for unmatched routes
  errorMessage: string = 'The page you are looking for cannot be found.';

  constructor(private route: ActivatedRoute, private location: Location) { }

  ngOnInit(): void {
    // Check for query parameters (HTTP errors)
    this.route.queryParams.subscribe(params => {
      if (params['code']) {
        this.errorCode = params['code'];
      }
      if (params['message']) {
        this.errorMessage = params['message'];
      }
    });
  }

  goBack(): void {
    this.location.back();
  }
}
