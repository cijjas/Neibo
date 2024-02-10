import { Component } from '@angular/core';
import {
  BackgroundDrawingComponent
} from "../../../components/background-drawing/background-drawing.component";
import {AppModule} from "../../../app.module";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    BackgroundDrawingComponent,
    AppModule
  ],
  templateUrl: './login.component.html',
})
export class LoginComponent {

}
