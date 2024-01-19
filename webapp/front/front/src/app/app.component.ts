import { Component, OnInit } from '@angular/core'
import { Post } from './post'
import { PostService } from './post.service'
import { Image } from './image'
import { ImageService } from './image.service'
import { HttpErrorResponse } from '@angular/common/http'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: '../../../../src/main/webapp/resources/css/home.css'
})
export class AppComponent implements OnInit {
  title = 'neibo'
  public postList: Post[] = []

  constructor(private postService: PostService){}

  ngOnInit() {
    this.getPosts()
  }

  public getPosts(): void {
    this.postService.getPosts(1, "FEED", [], "none", 1, 1, 10).subscribe(
      (response: Post[]) => {
        this.postList = response
      },
      (error: HttpErrorResponse) => {
        alert(error.message)
      }
    )
  }

}
