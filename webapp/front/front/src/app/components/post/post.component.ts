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
import { LikeCount } from '../../shared/models/likeCount';
import { LoggedInService } from '../../shared/services/loggedIn.service';

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
  public loggedUserUrn: string | undefined

  public likeCount: LikeCount;
  public isLikedByUser: boolean = false;

  constructor(
    private postService: PostService,
    private commentService: CommentService,
    private likeService: LikeService,
    private loggedInService: LoggedInService
  ) {}

  ngOnInit() {
    this.getPost();
    this.getComments();
    this.getLikeInformation();

    this.loggedInService.getLoggedUserURN().subscribe(
      (urn: string) => {
        this.loggedUserUrn = urn;  
      },
      (error: any) => {
        console.error('Error retrieving logged user URN:', error);
      }
    );
  }



  // toggleLike(): void {
  //   const likeForm: LikeForm = {
  //     likeId: 0,
  //     postUrn: this.self,
  //     self: 'user123',
  //   };

  //   this.likeService.addLike(likeForm).subscribe(
  //     () => {
  //       this.isLikedByUser = !this.isLikedByUser;
  //       this.likeCount += this.isLikedByUser ? 1 : -1;
  //     },
  //     (error: HttpErrorResponse) => {
  //       console.error('Error toggling like:', error);
  //     }
  //   );
  // }


  public getLikeInformation(): void {
    this.likeService.isLikedByUser(this.postUrn, this.loggedUserUrn)
      .subscribe(
        (isLikedByUser: boolean) => {
          this.isLikedByUser = isLikedByUser;
        }
      );
    //this.likeCount = this.post?.likes.length || 0;
  }
  public getComments(): void {
    this.commentService.getComments(this.postUrn, 1, 10)
      .subscribe(
        (comments: Comment[]) => {
          this.comments = comments;
        }
      )

  }
  public getPost(): void {
    this.postService.getPost(this.postUrn)
      .subscribe(
      (post) => {
        this.post = post;
      }
    );

  }

}
