import { Component, inject, OnInit } from '@angular/core';
import { HttpErrorResponse } from "@angular/common/http";
import { ActivatedRoute } from '@angular/router';

import { Comment, Post } from "../../shared/models/index";
import { PostService, CommentService, LikeService, UserSessionService } from "../../shared/services/index.service";

@Component({
  selector: 'app-post',
  templateUrl: './post-detail.component.html',
  styleUrls: ['../../app.component.css']
})
export class PostDetailComponent implements OnInit {
  postSelf!: string;
  route: ActivatedRoute = inject(ActivatedRoute);

  public post: Post | undefined;
  public comments: Comment[] = [];
  public loggedUserUrn: string | undefined;
  public likeCount: number = 0;
  public isLikedByUser: boolean = false;

  constructor(
    private postService: PostService,
    private commentService: CommentService,
    private likeService: LikeService,
    private userSessionService: UserSessionService
  ) { }

  ngOnInit(): void {
    this.postSelf = this.route.snapshot.paramMap.get('id')!;
    this.loadLoggedUserUrn();
    this.loadPostData();
  }

  private loadLoggedUserUrn(): void {
    this.userSessionService.getCurrentUser().subscribe(
      (user) => {
        if (user) {
          this.loggedUserUrn = user.self;
        }
      },
      (error: any) => {
        console.error('Error retrieving logged user URN:', error);
      }
    );
  }

  private loadPostData(): void {
    this.getPost();
  }




  public getPost(): void {
    this.postService.getPost(this.postSelf).subscribe({
      next: (post: Post) => {
        this.post = post;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching post:', error);
      }
    });
  }
}