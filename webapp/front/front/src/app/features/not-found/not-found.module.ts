import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotFoundPageComponent } from '@features/index';
import { SharedModule } from '@shared/shared.module';
import { NotFoundRoutingModule } from './not-found-routing.module';

@NgModule({
    declarations: [NotFoundPageComponent],
    imports: [
        CommonModule,
        SharedModule,
        NotFoundRoutingModule
    ],
    exports: [NotFoundPageComponent]
})
export class NotFoundModule { }
