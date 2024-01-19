import { Component, OnInit } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { PostForm } from './postForm';
import { PostService } from './post.service';
import { ImageForm } from './imageForm';
import { ImageService } from './image.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'] // Correct the property name to 'styleUrls'
})
export class AppComponent implements OnInit {
  title = 'neibo';
  public postList: PostForm[] = [];

  constructor(private postService: PostService, private imageService: ImageService) {}

  ngOnInit() {
    this.getPosts();
  }

  public getPosts(): void {
    this.postService.getPosts(1, 'FEED', [], 'none', 1, 1, 10).pipe(
      switchMap((response: PostForm[]) => {
        return forkJoin(
          response.map(post =>
            this.imageService.getImage(this.extractImageIdFromUri(post.imageFile)).pipe(
              map(image => ({ post, image }))
            )
          )
        );
      })
    ).subscribe(
      (postImagePairs: { post: PostForm, image: ImageForm }[]) => {
        // Assuming you have a property for postImagePairs
        this.postList = postImagePairs.map(pair => pair.post);
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  // Add a method to extract the image ID from the URI
  private extractImageIdFromUri(uri: string): number {
    // Implement the logic to extract the image ID from the URI
    // For example, extract the ID from the end of the URI
    const parts = uri.split('/');
    return +parts[parts.length - 1];
  }
}
