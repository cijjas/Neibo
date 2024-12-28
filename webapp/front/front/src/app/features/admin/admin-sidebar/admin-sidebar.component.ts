// admin-sidebar.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-sidebar',
  templateUrl: './admin-sidebar.component.html',
})
export class AdminSidebarComponent {
  constructor(private router: Router) { }

  isActive(route: string): boolean {
    return this.router.url === route;
  }
}
