import { Routes } from '@angular/router'
import { AppComponent } from './app.component'
import { PostComponent } from './components/post/post.component'
import { TestComponent } from './components/test/test.component'
import { FeedComponent } from './modules/feed/feed.component'
import { ServicesComponent } from './modules/services/services.component'
import { MarketplaceComponent } from './modules/marketplace/marketplace.component'
import { ReservationsComponent } from './modules/reservations/reservations.component'
import { InformationComponent } from './modules/information/information.component'

import { NotFoundComponent } from "./modules/not-found/not-found.component";
import { LoginComponent } from "./modules/auth/login/login.component";
import { authGuard } from "./shared/guards/auth.guard";

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'login', component: LoginComponent },
  { path: 'test', component: TestComponent },
  { path: 'feed', component: FeedComponent, canActivate: [authGuard] },
  { path: 'post/:postId', component: PostComponent, canActivate: [authGuard] },
  { path: 'services', component: ServicesComponent, canActivate: [authGuard] },
  { path: 'marketplace', component: MarketplaceComponent, canActivate: [authGuard] },
  { path: 'reservations', component: ReservationsComponent, canActivate: [authGuard] },
  { path: 'information', component: InformationComponent, canActivate: [authGuard] },
  { path: '**', component: NotFoundComponent }
];
