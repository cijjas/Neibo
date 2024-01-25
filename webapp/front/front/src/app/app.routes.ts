import { Routes } from '@angular/router'
import { AppComponent } from './app.component'
import { PostComponent } from './components/post/post.component'
import { FeedComponent } from './components/feed/feed.component'

export const routes: Routes = [
    // Add routes here
    { path: '', component: FeedComponent },
    { path: 'post', component: PostComponent}
]
