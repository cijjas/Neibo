import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['../../app.component.css']
})
export class PostComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  postId: number = this.route.snapshot.params['postId'];
}
