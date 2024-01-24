import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Post } from './shared/models/post';
import { PostService } from './shared/services/post.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  public postList: Post[] = [];

  constructor(private postService: PostService) {}

  ngOnInit() {
    this.getPosts();
  }

  public getPosts(): void {

    this.postService.getPosts(1, 'Feed', [], 'none', 1, 1, 10).subscribe(
      (response: Post[]) => {

        this.postList = response;
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }
}
