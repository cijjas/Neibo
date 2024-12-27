import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin-neighbors-requests-page',
  templateUrl: './admin-neighbors-requests-page.component.html',
})
export class AdminNeighborsRequestsPageComponent implements OnInit {

  neighbors: boolean = true;  // or this might come from a route param or state
  verified: boolean = false;  // same idea
  users: any[] = [];

  constructor() { }

  ngOnInit(): void {
    // Example: fetch neighbors from a service
    // this.userService.getUsers(...).subscribe(res => this.users = res.users);
    this.users = [
      {
        id: 1,
        name: 'John',
        surname: 'Doe',
        email: 'johndoe@example.com',
        identification: '1234567',
        image: 'assets/img/default-user.png'
      },
      // ...
    ];
  }

  rejectUser(userId: number) {
    console.log('Reject user with ID:', userId);
    // call service to reject user
  }

  verifyUser(userId: number) {
    console.log('Verify user with ID:', userId);
    // call service to accept user
  }
}
