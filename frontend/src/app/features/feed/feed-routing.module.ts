import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  FeedPageComponent,
  FeedPostDetailPageComponent,
} from '@features/index';

import { FeedCreatePostPageComponent } from '@features/index';
import { postResolver } from '@shared/resolvers/post.resolver';

const routes: Routes = [
  { path: '', component: FeedPageComponent },
  { path: 'new', component: FeedCreatePostPageComponent },
  {
    path: ':id',
    component: FeedPostDetailPageComponent,
    resolve: { post: postResolver },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FeedRoutingModule {}
