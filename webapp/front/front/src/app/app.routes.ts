
import { Routes } from '@angular/router'

import {
  PostDetailComponent
  , FeedComponent
  , ServicesComponent
  , MarketplaceComponent
  , ReservationsComponent
  , InformationComponent
  , CreatePostComponent
  , ProductDetailComponent
  , ProductSellComponent
  , ServiceProfilePageComponent
  , EventPageComponent
  , NotFoundComponent
  , LoginComponent
  , CalendarComponent
  , UserProfileComponent
  , ChooseTimeComponent
  , ProductEditComponent
  , BuyerHubComponent
  , SellerHubComponent
  , ListingRequestsComponent
  , AdminComponent
} from '@features/index'

import { RoleGuard } from '@core/index'
import { Roles } from '@shared/index'


export const routes: Routes = [
  // Admin
  { path: 'admin/publish', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/neighbors/requests', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/neighbors/list', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/service-providers/requests', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/service-providers/list', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/amenities', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/information', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },

  // Amenities
  { path: 'amenities', component: ReservationsComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'amenities/choose-time', component: ChooseTimeComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'reservations', component: ReservationsComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'reservations/choose-time', component: ChooseTimeComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Authentication
  { path: '', component: LoginComponent },
  { path: 'login', component: LoginComponent },


  // Calendar
  { path: 'calendar', component: CalendarComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'calendar/events/:id', component: EventPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // feed
  { path: 'posts', component: FeedComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'posts/:id', component: PostDetailComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'posts/create', component: CreatePostComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Information
  { path: 'information', component: InformationComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Marketplace
  { path: 'marketplace', component: MarketplaceComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/buyer-hub/:mode', component: BuyerHubComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/seller-hub/:mode', component: SellerHubComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/create', component: ProductSellComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/:id', component: ProductDetailComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/:id/edit', component: ProductEditComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/:id/requests', component: ListingRequestsComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Services
  { path: 'services', component: ServicesComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'services/profile/:id', component: ServiceProfilePageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Service providers
  // { path: 'services/profiles/:id', component: ServicesComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  // { path: 'services/feed/', component: ServicesComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  // { path: 'services/all-neighborhoods/', component: ServicesComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // User Profile
  { path: 'user/:id', component: UserProfileComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // 404 and Wildcard
  { path: 'not-found', component: NotFoundComponent },
  { path: '**', redirectTo: '/not-found', pathMatch: 'full' },
];