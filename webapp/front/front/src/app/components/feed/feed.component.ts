import { Component, OnInit } from '@angular/core';
import { Post } from '../../shared/models/post';
import { PostService } from '../../shared/services/post.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['../../app.component.css']
})
export class FeedComponent implements OnInit {
  public postList: Post[] = [];

  constructor(private postService: PostService) {}

  ngOnInit() {
    this.getPosts();
  }


  public getPosts(): void {
    this.postService.getPosts(1, 'Feed', [], 'none', 1, 1, 10)
      .subscribe(
      (posts: Post[]) => {
        this.postList = posts;
      },
      (error: HttpErrorResponse) => {
        // Handle error if needed
      }
    );
  }
}
