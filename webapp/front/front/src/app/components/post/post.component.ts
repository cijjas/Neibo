import { Component, inject, OnInit } from '@angular/core';
import { HttpErrorResponse } from "@angular/common/http";
import { ActivatedRoute } from '@angular/router';

import { Comment, Post } from "../../shared/models/index";
import { PostService, CommentService, LikeService, UserSessionService } from "../../shared/services/index.service";

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['../../app.component.css']
})
export class PostComponent implements OnInit {

  route: ActivatedRoute = inject(ActivatedRoute);
  postUrn: string = this.route.snapshot.params['post'];

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
    this.loadLoggedUserUrn();
    this.loadPostData();
  }

  private loadLoggedUserUrn(): void {
    this.userSessionService.getLoggedUser().subscribe(
      (user) => {
        if (user) {
          this.loggedUserUrn = user.self;
          this.getLikeInformation();
        }
      },
      (error: any) => {
        console.error('Error retrieving logged user URN:', error);
      }
    );
  }

  private loadPostData(): void {
    this.getPost();
    this.getComments();
  }

  public toggleLike(): void {
    if (!this.loggedUserUrn || !this.postUrn) {
      console.error("Cannot toggle like without a logged user or post URN");
      return;
    }

    // this.likeService.toggleLike(this.postUrn, this.loggedUserUrn).subscribe(
    //   () => {
    //     this.isLikedByUser = !this.isLikedByUser;
    //     this.likeCount += this.isLikedByUser ? 1 : -1;
    //   },
    //   (error: HttpErrorResponse) => {
    //     console.error('Error toggling like:', error);
    //   }
    // );
  }

  public getLikeInformation(): void {
    if (!this.postUrn || !this.loggedUserUrn) return;

    // this.likeService.isLikedByUser(this.postUrn, this.loggedUserUrn).subscribe(
    //   (isLikedByUser: boolean) => {
    //     this.isLikedByUser = isLikedByUser;
    //   },
    //   (error: HttpErrorResponse) => {
    //     console.error('Error fetching like information:', error);
    //   }
    // );

    // this.likeService.getLikeCount(this.postUrn).subscribe(
    //   (likeCount: number) => {
    //     this.likeCount = likeCount;
    //   },
    //   (error: HttpErrorResponse) => {
    //     console.error('Error fetching like count:', error);
    //   }
    // );
  }

  public getComments(page: number = 1, size: number = 10): void {
    this.commentService.getComments(this.postUrn, { page, size }).subscribe({
      next: (comments: Comment[]) => {
        this.comments = comments;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching comments:', error);
      }
    });
  }

  public getPost(): void {
    this.postService.getPost(this.postUrn).subscribe({
      next: (post: Post) => {
        this.post = post;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching post:', error);
      }
    });
  }

}
