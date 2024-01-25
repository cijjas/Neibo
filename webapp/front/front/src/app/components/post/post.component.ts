import {Component, inject, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {Post} from "../../shared/models/post";
import {PostService} from "../../shared/services/post.service";
import {Comment} from "../../shared/models/comment";
import {CommentService} from "../../shared/services/comment.service";
import {HttpErrorResponse} from "@angular/common/http";
import {Channel} from "../../shared/models/channel";

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
  constructor(
    private postService: PostService,
    private commentService: CommentService
  ) {}

  ngOnInit() {


    this.getPost();
    this.getComments();
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
