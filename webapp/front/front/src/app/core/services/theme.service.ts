// import { Injectable } from '@angular/core';
// import { BehaviorSubject } from 'rxjs';
// import { UserService } from '@shared/index';
// import { UserSessionService } from './user-session.service';

// @Injectable({
//   providedIn: 'root',
// })
// export class ThemeService {
//   private darkModeSubject = new BehaviorSubject<boolean>(false);
//   darkMode$ = this.darkModeSubject.asObservable();

//   constructor(
//     private userService: UserService,
//     private userSessionService: UserSessionService
//   ) {
//     this.initializeTheme();
//   }

//   setDarkMode(isDarkMode: boolean): void {
//     this.darkModeSubject.next(isDarkMode);
//     document.documentElement.classList.toggle('dark-mode', isDarkMode);
//     localStorage.setItem('darkMode', JSON.stringify(isDarkMode)); // Persist preference locally
//   }

//   initializeTheme(): void {
//     const storedDarkMode = JSON.parse(
//       localStorage.getItem('darkMode') || 'false'
//     );
//     this.setDarkMode(storedDarkMode);

//     // Optionally, synchronize with backend
//     this.userSessionService.getCurrentUser().subscribe((user) => {
//       if (user && user.darkMode !== storedDarkMode) {
//         this.setDarkMode(user.darkMode);
//       }
//     });
//   }

//   toggleDarkMode(): void {
//     const currentDarkMode = this.darkModeSubject.value;
//     const newDarkMode = !currentDarkMode;
//     this.setDarkMode(newDarkMode);

//     // Update backend
//     this.userSessionService.getCurrentUser().subscribe((user) => {
//       if (user) {
//         this.userService.toggleDarkMode(user).subscribe((updatedUser) => {
//           this.userSessionService.setUserInformation(updatedUser);
//         });
//       }
//     });
//   }
// }
