import { Component, OnInit } from '@angular/core';
import { UserService, UserSessionService } from '../../shared/services/index.service';
import { User } from '../../shared/models/index';

@Component({
  selector: 'user-profile-widget',
  templateUrl: './user-profile-widget.component.html',
})
export class UserProfileWidgetComponent implements OnInit {
  loggedUser: User | null = null;
  profileImageUrl: string = 'assets/images/roundedPlaceholder.png'; // Default image

  constructor(
    private userService: UserService,
    private userSessionService: UserSessionService
  ) { }

  ngOnInit() {
    this.userSessionService.getCurrentUser().subscribe((user: User) => {
      this.loggedUser = user;

      if (user?.image) {
        // Convert Uint8Array to Base64
        // const base64Image = this.convertArrayToBase64(user.image);
        // this.profileImageUrl = `data:image/png;base64,${base64Image}`;
      }
    });
  }

  private convertArrayToBase64(byteArray: Uint8Array): string {
    const binaryString = Array.from(byteArray)
      .map(byte => String.fromCharCode(byte))
      .join('');
    return btoa(binaryString);
  }
}
