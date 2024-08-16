// ./app/components/navbar/navbar.component.ts
import {Component, OnInit} from '@angular/core';
import {UserService} from "../../shared/services/user.service";
import {User, UserDto} from "../../shared/models/user";
import { LoggedInService } from '../../shared/services/loggedIn.service';

@Component({
  selector: 'user-profile-widget',
  templateUrl: './user-profile-widget.component.html',
})
export class UserProfileWidgetComponent implements OnInit {
  loggedUser: UserDto | null = null; // Initialize to null or a default user model

  constructor(
    private userService: UserService,
    private loggedInService: LoggedInService
  ) {}

  ngOnInit() {
    this.loggedInService.getLoggedUser().subscribe((user) => {
      this.loggedUser = user;
    });
  }
}
