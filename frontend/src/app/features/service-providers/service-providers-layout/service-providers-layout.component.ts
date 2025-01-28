import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-service-providers-layout',
  templateUrl: './service-providers-layout.component.html',
})
export class ServiceProvidersLayoutComponent {
  isServicesRoute: boolean = false;

  constructor(private router: Router) { }

  ngOnInit() {
    // Update `isServicesRoute` whenever the route changes
    this.router.events.subscribe(() => {
      const url = this.router.url;
      this.isServicesRoute = url === '/services' || url.startsWith('/services?');
    });
  }
}
