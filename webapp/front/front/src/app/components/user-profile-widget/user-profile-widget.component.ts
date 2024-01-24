// ./app/components/navbar/navbar.component.ts
import {Component, OnInit} from '@angular/core';
import {UserService} from "../../shared/services/user.service";
import {User} from "../../shared/models/user";

@Component({
  selector: 'user-profile-widget',
  templateUrl: './user-profile-widget.component.html',
})
export class UserProfileWidgetComponent implements OnInit {
  loggedUser: User | null = null; // Initialize to null or a default user model

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getUser(1,1).subscribe((user) => {
      this.loggedUser = user;
    });
  }
}
