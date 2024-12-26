import { Roles } from './shared/models'

import { Routes } from '@angular/router'
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
import { EventPageComponent } from './modules/event-page/event-page.component';

import { NotFoundComponent } from "./modules/not-found/not-found.component";
import { LoginComponent } from "./modules/auth/login/login.component";
import { CalendarComponent } from './modules/calendar/calendar.component'
import { UserProfileComponent } from './modules/user-profile/user-profile.component'
import { ChooseTimeComponent } from './modules/choose-time/choose-time.component'
import { ProductEditComponent } from './modules/marketplace/product-edit/product-edit.component';
import { BuyerHubComponent } from './modules/marketplace/buyer-hub/buyer-hub.component';
import { SellerHubComponent } from './modules/marketplace/seller-hub/seller-hub.component';
import { ListingRequestsComponent } from './modules/marketplace/listing-requests/listing-requests.component';
import { RoleGuard } from './shared/guards/role.guard';
import { AdminComponent } from './modules/admin/admin.component'

export const routes: Routes = [
  // Authentication
  { path: '', component: LoginComponent },
  { path: 'login', component: LoginComponent },

  // Posts and User Profile
  { path: 'posts', component: FeedComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'post/:id', component: PostDetailComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'create-post', component: CreatePostComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'user/:id', component: UserProfileComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Information
  { path: 'information', component: InformationComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  { path: 'reservations', component: ReservationsComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'choose-time', component: ChooseTimeComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Calendar
  { path: 'calendar', component: CalendarComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'calendar/events/:id', component: EventPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Marketplace
  { path: 'marketplace', component: MarketplaceComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/buyer-hub/:mode', component: BuyerHubComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/seller-hub/:mode', component: SellerHubComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/create-listing', component: ProductSellComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/:id', component: ProductDetailComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/:id/edit', component: ProductEditComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/:id/requests', component: ListingRequestsComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Services
  { path: 'services', component: ServicesComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'services/profile/:id', component: ServiceProfilePageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // ADMIN
  { path: 'admin', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },

  // 404 and Wildcard
  { path: 'not-found', component: NotFoundComponent },
  { path: '**', redirectTo: '/not-found', pathMatch: 'full' },
];