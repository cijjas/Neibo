// ./app/components/navbar/navbar.component.ts
import {Component, Input} from '@angular/core';
import {Post} from "../../shared/models/post";
import { LikeService } from '../../shared/services/like.service';

@Component({
  selector: 'blogpost',
  templateUrl: './blogpost.component.html',
})
export class BlogpostComponent{
  @Input() post!: Post;

  constructor(likeService : LikeService) {}

}
