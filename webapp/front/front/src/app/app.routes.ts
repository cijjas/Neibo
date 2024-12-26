import { Routes } from '@angular/router'
import { authGuard } from "./shared/guards/auth.guard";
import { AppComponent } from './app.component'
import { PostDetailComponent } from './modules/post-detail/post-detail.component'
import { FeedComponent } from './modules/feed/feed.component'
import { ServicesComponent } from './modules/services/services/services.component'
import { MarketplaceComponent } from './modules/marketplace/marketplace/marketplace.component'
import { ReservationsComponent } from './modules/reservations/reservations.component'
import { InformationComponent } from './modules/information/information.component'
import { CreatePostComponent } from './modules/create-post/create-post.component';
import { ProductDetailComponent } from './modules/marketplace/product-detail/product-detail.component';
import { ProductSellComponent } from './modules/marketplace/product-sell/product-sell.component';
import { ServiceProfilePageComponent } from './modules/services/service-profile-page/service-profile-page.component';

import { NotFoundComponent } from "./modules/not-found/not-found.component";
import { LoginComponent } from "./modules/auth/login/login.component";
import { CalendarComponent } from './modules/calendar/calendar.component'
import { UserProfileComponent } from './modules/user-profile/user-profile.component'
import { ChooseTimeComponent } from './modules/choose-time/choose-time.component'
import { ProductEditComponent } from './modules/marketplace/product-edit/product-edit.component';
import { BuyerHubComponent } from './modules/marketplace/buyer-hub/buyer-hub.component';
import { SellerHubComponent } from './modules/marketplace/seller-hub/seller-hub.component';
import { ListingRequestsComponent } from './modules/marketplace/listing-requests/listing-requests.component';


export const routes: Routes = [
  // Authentication
  { path: '', component: LoginComponent },
  { path: 'login', component: LoginComponent },

  // Posts and User Profile
  { path: 'posts', component: FeedComponent, canActivate: [authGuard] },
  { path: 'post/:id', component: PostDetailComponent, canActivate: [authGuard] },
  { path: 'create-post', component: CreatePostComponent, canActivate: [authGuard] },
  { path: 'user/:id', component: UserProfileComponent, canActivate: [authGuard] },

  // Information
  { path: 'information', component: InformationComponent, canActivate: [authGuard] },
  { path: 'calendar', component: CalendarComponent, canActivate: [authGuard] },
  { path: 'reservations', component: ReservationsComponent, canActivate: [authGuard] },
  { path: 'choose-time', component: ChooseTimeComponent, canActivate: [authGuard] },

  // Marketplace
  { path: 'marketplace', component: MarketplaceComponent, canActivate: [authGuard] },
  {
    path: 'marketplace/buyer-hub/:mode',
    component: BuyerHubComponent,
    canActivate: [authGuard],
  },
  {
    path: 'marketplace/seller-hub/:mode',
    component: SellerHubComponent,
    canActivate: [authGuard],
  },
  { path: 'marketplace/create-listing', component: ProductSellComponent, canActivate: [authGuard] },
  { path: 'marketplace/products/:id', component: ProductDetailComponent, canActivate: [authGuard] },
  { path: 'marketplace/products/:id/edit', component: ProductEditComponent, canActivate: [authGuard] },
  { path: 'marketplace/products/:id/requests', component: ListingRequestsComponent, canActivate: [authGuard] },


  // Services
  { path: 'services', component: ServicesComponent, canActivate: [authGuard] },
  { path: 'services/profile/:id', component: ServiceProfilePageComponent, canActivate: [authGuard] },

  // 404 and Wildcard
  { path: 'not-found', component: NotFoundComponent },
  { path: '**', redirectTo: '/not-found', pathMatch: 'full' },
];