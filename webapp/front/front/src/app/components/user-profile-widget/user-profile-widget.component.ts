import { Component, OnInit } from '@angular/core';
import { UserService } from '../../shared/services/user.service';
import { LoggedInService } from '../../shared/services/loggedIn.service';
import { UserDto } from '../../shared/models/user';

@Component({
  selector: 'user-profile-widget',
  templateUrl: './user-profile-widget.component.html',
})
export class UserProfileWidgetComponent implements OnInit {
  loggedUser: UserDto | null = null;
  profileImageUrl: string = 'assets/images/roundedPlaceholder.png'; // Default image

  constructor(
    private userService: UserService,
    private loggedInService: LoggedInService
  ) { }

  ngOnInit() {
    this.loggedInService.getLoggedUser().subscribe((user) => {
      this.loggedUser = user;
      console.log(user._links);
      if (user?._links?.profilePicture) {
        this.profileImageUrl = `/images/${user._links.profilePicture}`;
      }
    });
  }
}
