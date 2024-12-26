
import { Routes } from '@angular/router'

import {
  FeedPostDetailPageComponent
  , FeedPageComponent
  , ServiceProvidersPageComponent
  , MarketplacePageComponent
  , AmenitiesReservationsPageComponent
  , InformationPageComponent
  , FeedCreatePostPageComponent
  , MarketplaceProductDetailPageComponent
  , MarketplaceProductSellPageComponent
  , ServiceProvidersDetailPageComponent
  , CalendarEventPageComponent
  , NotFoundPageComponent
  , LoginPageComponent
  , CalendarPageComponent
  , UserProfilePageComponent
  , AmenitiesChooseTimePageComponent
  , MarketplaceProductEditPageComponent
  , MarketplaceDashboardBuyerPageComponent
  , MarketplaceDashboardSellerPageComponent
  , MarketplaceProductRequestsPageComponent
  , AdminPageComponent
} from '@features/index'

import { RoleGuard } from '@core/index'
import { Roles } from '@shared/index'


export const routes: Routes = [
  // Admin
  { path: 'admin/publish', component: AdminPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/neighbors/requests', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/neighbors/list', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/service-providers/requests', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/service-providers/list', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/amenities', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },
  // { path: 'admin/information', component: AdminComponent, canActivate: [RoleGuard], data: { roles: [Roles.ADMINISTRATOR] } },

  // Amenities
  { path: 'amenities', component: AmenitiesReservationsPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'amenities/choose-time', component: AmenitiesChooseTimePageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'reservations', component: AmenitiesReservationsPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'reservations/choose-time', component: AmenitiesChooseTimePageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Authentication
  { path: '', component: LoginPageComponent },
  { path: 'login', component: LoginPageComponent },


  // Calendar
  { path: 'calendar', component: CalendarPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'calendar/events/:id', component: CalendarEventPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // feed
  { path: 'posts', component: FeedPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'posts/:id', component: FeedPostDetailPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'posts/new', component: FeedCreatePostPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Information
  { path: 'information', component: InformationPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Marketplace
  { path: 'marketplace', component: MarketplacePageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/buyer-hub/:mode', component: MarketplaceDashboardBuyerPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/seller-hub/:mode', component: MarketplaceDashboardSellerPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/new', component: MarketplaceProductSellPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/:id', component: MarketplaceProductDetailPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/:id/edit', component: MarketplaceProductEditPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'marketplace/products/:id/requests', component: MarketplaceProductRequestsPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Services
  { path: 'services', component: ServiceProvidersPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  { path: 'services/profile/:id', component: ServiceProvidersDetailPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // Service providers
  // { path: 'services/profiles/:id', component: ServiceProvidersPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  // { path: 'services/feed/', component: ServiceProvidersPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },
  // { path: 'services/all-neighborhoods/', component: ServiceProvidersPageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // User Profile
  { path: 'user/:id', component: UserProfilePageComponent, canActivate: [RoleGuard], data: { roles: [Roles.NEIGHBOR, Roles.ADMINISTRATOR] } },

  // 404 and Wildcard
  { path: 'not-found', component: NotFoundPageComponent },
  { path: '**', redirectTo: '/not-found', pathMatch: 'full' },
];
