import { Routes } from '@angular/router'
import { AppComponent } from './app.component'
import { PostComponent } from './components/post/post.component'
import { FeedComponent } from './components/feed/feed.component'
import { ServicesComponent } from './components/services/services.component'
import { MarketplaceComponent } from './components/marketplace/marketplace.component'
import { ReservationsComponent } from './components/reservations/reservations.component'
import { InformationComponent } from './components/information/information.component'

export const routes: Routes = [
    // Add routes here
    { path: '', component: FeedComponent },
    { path: 'post/:postId', component: PostComponent },
    { path: 'services', component: ServicesComponent },
    { path: 'marketplace', component: MarketplaceComponent },
    { path: 'reservations', component: ReservationsComponent },
    { path: 'information', component: InformationComponent}
]
