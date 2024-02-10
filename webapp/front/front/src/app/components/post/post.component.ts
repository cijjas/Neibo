import {Component, inject, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Post } from "../../shared/models/post";
import {PostService} from "../../shared/services/post.service";
import {Comment} from "../../shared/models/comment";
import {CommentService} from "../../shared/services/comment.service";
import {HttpErrorResponse} from "@angular/common/http";
import {Channel} from "../../shared/models/channel";
import {LikeService} from "../../shared/services/like.service";
import {LikeForm} from "../../shared/models/like";

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['../../app.component.css']
})
export class PostComponent implements OnInit {

  route: ActivatedRoute = inject(ActivatedRoute);
  postId: number = this.route.snapshot.params['postId'];

  public post: Post | undefined;
  public comments: Comment[] = [];

  public likeCount: number = 0;
  public isLikedByUser: boolean = false;

  constructor(
    private postService: PostService,
    private commentService: CommentService,
    private likeService: LikeService
  ) {}

  ngOnInit() {
    this.getPost();
    this.getComments();
    this.getLikeInformation();
  }



  toggleLike(): void {
    const likeForm: LikeForm = {
      likeId: 0,
      postId: this.postId,
      self: 'user123',
    };

    this.likeService.addLike(likeForm).subscribe(
      () => {
        this.isLikedByUser = !this.isLikedByUser;
        this.likeCount += this.isLikedByUser ? 1 : -1;
      },
      (error: HttpErrorResponse) => {
        console.error('Error toggling like:', error);
      }
    );
  }


  public getLikeInformation(): void {
    this.likeService.isLikedByUser(this.postId,1)
      .subscribe(
        (isLikedByUser: boolean) => {
          this.isLikedByUser = isLikedByUser;
        },
        (error: HttpErrorResponse) => {
          console.log(error);
        }
      );
    this.likeCount = this.post?.likes.length || 0;
  }
  public getComments(): void {
    this.commentService.getComments(1, this.postId, 1, 10)
      .subscribe(
        (comments: Comment[]) => {
          this.comments =comments;
        },
        (error: HttpErrorResponse) => {
          console.log(error);
        }
      )

  }
  public getPost(): void {
    this.postService.getPost(1, this.postId)
      .subscribe(
      (post) => {
        this.post = post;
      },
      (error) => {
      }
    );

  }

}
