import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-not-found-page',
  templateUrl: './not-found-page.component.html',
})
export class NotFoundPageComponent implements OnInit {
  errorCode: string = '404'; // Default code for unmatched routes
  errorMessage: string = '';

  constructor(private route: ActivatedRoute, private router: Router) {}

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
    this.router.navigate(['/']);
  }
}
