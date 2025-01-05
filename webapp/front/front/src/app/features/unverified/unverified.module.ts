import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UnverifiedPageComponent } from './unverified-page/unverified-page.component';
import { SharedModule } from "@shared/shared.module";

const routes: Routes = [
    // This route is just '' because the module is lazy-loaded
    { path: '', component: UnverifiedPageComponent },
];

@NgModule({
    declarations: [UnverifiedPageComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule]
})
export class UnverifiedModule { }