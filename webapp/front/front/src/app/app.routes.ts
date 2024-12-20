import { Routes } from '@angular/router'
import { authGuard } from "./shared/guards/auth.guard";
import { AppComponent } from './app.component'
import { PostDetailComponent } from './modules/post-detail/post-detail.component'
import { FeedComponent } from './modules/feed/feed.component'
import { ServicesComponent } from './modules/services/services.component'
import { MarketplaceComponent } from './modules/marketplace/marketplace.component'
import { ReservationsComponent } from './modules/reservations/reservations.component'
import { InformationComponent } from './modules/information/information.component'
import { CreatePostComponent } from './modules/create-post/create-post.component';
import { ProductDetailComponent } from './modules/product-detail/product-detail.component';
import { ProductSellComponent } from './modules/product-sell/product-sell.component';

import { NotFoundComponent } from "./modules/not-found/not-found.component";
import { LoginComponent } from "./modules/auth/login/login.component";
import { CalendarComponent } from './modules/calendar/calendar.component'
import { UserProfileComponent } from './modules/user-profile/user-profile.component'
import { ChooseTimeComponent } from './modules/choose-time/choose-time.component'

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'login', component: LoginComponent },
  { path: 'posts', component: FeedComponent, canActivate: [authGuard] },
  { path: 'post/:id', component: PostDetailComponent, canActivate: [authGuard] },
  { path: 'user/:id', component: UserProfileComponent, canActivate: [authGuard] },
  { path: 'create-post', component: CreatePostComponent, canActivate: [authGuard] },
  { path: 'services', component: ServicesComponent, canActivate: [authGuard] },
  { path: 'marketplace', component: MarketplaceComponent, canActivate: [authGuard] },
  { path: 'marketplace/products/:id', component: ProductDetailComponent, canActivate: [authGuard] },
  { path: 'marketplace/create-listing', component: ProductSellComponent, canActivate: [authGuard] },
  { path: 'reservations', component: ReservationsComponent, canActivate: [authGuard] },
  { path: 'choose-time', component: ChooseTimeComponent, canActivate: [authGuard] },
  { path: 'information', component: InformationComponent, canActivate: [authGuard] },
  { path: 'calendar', component: CalendarComponent, canActivate: [authGuard] },
  { path: 'not-found', component: NotFoundComponent },
  { path: '**', redirectTo: '/not-found', pathMatch: 'full' },
  { path: '**', component: NotFoundComponent }
];
