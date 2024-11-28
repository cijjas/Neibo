import { Component, OnInit } from '@angular/core';
import { UserService } from '../../shared/services/user.service'
import { User } from '../../shared/models/user';

@Component({
  selector: 'app-test',
  standalone: true,
  templateUrl: './test.component.html',
  providers: [UserService], // Ensure the service is provided here or in the module
})
export class TestComponent implements OnInit {
  user: User | null = null; // Store fetched user data
  error: string | null = null; // Store error messages, if any

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    console.log("im doing something");

    const userUrl = 'http://localhost:8080/users/4'; // Replace with a valid user endpoint

    this.userService.getUser(userUrl).subscribe({
      next: (userData) => {
        this.user = userData; // Store fetched user
        console.log('Fetched user:', userData);
      },
      error: (err) => {
        this.error = 'Failed to fetch user';
        console.error('Error fetching user:', err);
      },
    });
  }
}
