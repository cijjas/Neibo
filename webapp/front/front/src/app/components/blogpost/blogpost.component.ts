// ./app/components/navbar/navbar.component.ts
import {Component, Input} from '@angular/core';

@Component({
  selector: 'blogpost',
  templateUrl: './blogpost.component.html',
})
export class BlogpostComponent{
  @Input() postID: number | undefined;
  @Input() postNeighborMail: string | undefined;
  @Input() postDate: Date | undefined;
  @Input() postTitle: string | undefined;
  @Input() postDescription: string | undefined;
  @Input() postImage: number | undefined;
  @Input() postLikes: number | undefined;
}
