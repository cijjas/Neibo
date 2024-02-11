import { Routes } from '@angular/router'
import { AppComponent } from './app.component'
import { PostComponent } from './components/post/post.component'
import { FeedComponent } from './modules/feed/feed.component'
import { ServicesComponent } from './modules/services/services.component'
import { MarketplaceComponent } from './modules/marketplace/marketplace.component'
import { ReservationsComponent } from './modules/reservations/reservations.component'
import { InformationComponent } from './modules/information/information.component'

import {NotFoundComponent} from "./modules/not-found/not-found.component";
import {LoginComponent} from "./modules/auth/login/login.component";
import {authGuard} from "./shared/guards/auth.guard";

export const routes: Routes = [
  { path: '', component: LoginComponent, canActivate: [authGuard] }, /*Chequea condicion authGuard y si no cumple va a /login*/
  { path: 'login', component: LoginComponent},
  { path: 'feed', component: FeedComponent },
  { path: 'post/:postId', component: PostComponent },
  { path: 'services', component: ServicesComponent },
  { path: 'marketplace', component: MarketplaceComponent },
  { path: 'reservations', component: ReservationsComponent },
  { path: 'information', component: InformationComponent},
  {path: '**', component: NotFoundComponent}
]
