// ./app/components/navbar/navbar.component.ts
import {Component, Input} from '@angular/core';
import {Post} from "../../shared/models/post";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'blogpost',
  templateUrl: './blogpost.component.html',
})
export class BlogpostComponent{
  @Input() post: Post | undefined;



  generateTagUrl(tag: string): string {
    return `${environment.apiBaseUrl}/tags?tag=${tag}`;
  }

  generatePostUrl(postId: number | undefined): string {
    const validPostId = postId || 0; // Replace 0 with the desired default value
    return `${environment.apiBaseUrl}/posts/${validPostId}`;
  }
}
