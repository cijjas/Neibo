import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';


import { LoginDialogComponent } from './login-dialog/login-dialog.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { SignupDialogComponent } from './signup-dialog/signup-dialog.component';

import { SharedModule } from '@shared/index';
import { AuthRoutingModule } from './auth-routing.module';


@NgModule({
    declarations: [
        LoginPageComponent,
        LoginDialogComponent,
        SignupDialogComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        AuthRoutingModule
    ],

})
export class AuthModule { }
