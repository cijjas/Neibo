import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin-service-providers-requests-page',
  templateUrl: './admin-service-providers-requests-page.component.html',
})
export class AdminServiceProvidersRequestsPageComponent implements OnInit {

  inWorkers: boolean = true; // or loaded from some route/data
  verified: boolean = false;
  workers: any[] = [];

  constructor() { }

  ngOnInit(): void {
    // Example: call a WorkerService
    // this.workerService.getWorkers(...).subscribe(res => this.workers = res.workers);
    this.workers = [
      {
        id: 10,
        phoneNumber: '555-1234',
        businessName: 'Example Biz',
        user: {
          id: 99,
          name: 'Jane',
          surname: 'Doe',
          email: 'janedoe@example.com',
          identification: 7654321,
          image: 'assets/img/default-user.png'
        }
      },
      // ...
    ];
  }

  verifyWorker(workerId: number) {
    console.log('Verifying worker with ID:', workerId);
    // call service to accept
  }

  rejectWorker(workerId: number) {
    console.log('Rejecting worker with ID:', workerId);
    // call service to reject
  }
}
