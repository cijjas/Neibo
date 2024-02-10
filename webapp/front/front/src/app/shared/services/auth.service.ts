import {inject, Injectable} from "@angular/core";
import {UserService} from "./user.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  isLogged: Boolean = false;
  userService: UserService = inject(UserService);



}
