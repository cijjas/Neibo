
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


export const appRoutes: Routes = [
  // Redirect root to login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Admin
  // {
  //   path: 'admin',
  //   loadChildren: () =>
  //     import('@features/admin/admin.module').then((m) => m.AdminModule),
  //   canActivate: [RoleGuard],
  //   data: { roles: [Roles.ADMINISTRATOR] },
  // },

  // Amenities
  {
    path: 'amenities',
    loadChildren: () =>
      import('@features/amenities/amenities.module').then(
        (m) => m.AmenitiesModule
      ),
  },

  // Authentication
  {
    path: 'login',
    loadChildren: () =>
      import('@features/auth/auth.module').then((m) => m.AuthModule),
  },

  // Calendar
  {
    path: 'calendar',
    loadChildren: () =>
      import('@features/calendar/calendar.module').then((m) => m.CalendarModule),
  },

  // Feed
  {
    path: 'posts',
    loadChildren: () =>
      import('@features/feed/feed.module').then((m) => m.FeedModule),
  },

  // Information
  {
    path: 'information',
    loadChildren: () =>
      import('@features/information/information.module').then(
        (m) => m.InformationModule
      ),
  },

  // Marketplace
  {
    path: 'marketplace',
    loadChildren: () =>
      import('@features/marketplace/marketplace.module').then(
        (m) => m.MarketplaceModule
      ),
  },

  // Services
  {
    path: 'services',
    loadChildren: () =>
      import('@features/service-providers/service-providers.module').then(
        (m) => m.ServiceProvidersModule
      ),
  },

  // User Profile
  {
    path: 'user',
    loadChildren: () =>
      import('@features/user-profile/user-profile.module').then(
        (m) => m.UserProfileModule
      ),
  },

  // 404 and Wildcard
  {
    path: 'not-found',
    loadChildren: () =>
      import('@features/not-found/not-found.module').then(
        (m) => m.NotFoundModule
      ),
  },
  { path: '**', redirectTo: '/not-found', pathMatch: 'full' },
];

