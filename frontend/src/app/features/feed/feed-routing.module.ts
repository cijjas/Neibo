import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  FeedPageComponent,
  FeedPostDetailPageComponent,
} from '@features/index';

import { FeedCreatePostPageComponent } from '@features/index';

const routes: Routes = [
  { path: '', component: FeedPageComponent },
  { path: 'new', component: FeedCreatePostPageComponent },
  { path: ':id', component: FeedPostDetailPageComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FeedRoutingModule {}
