import { Routes } from '@angular/router';

import { RoleGuard, AuthGuard } from '@core/index';
import {
  MainLayoutComponent,
  PublicLayoutComponent,
  Role,
} from '@shared/index';

export const appRoutes: Routes = [
  // Redirect root to login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Authentication
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      {
        path: 'login',
        loadChildren: () =>
          import('@features/auth/auth.module').then(m => m.AuthModule),
        canActivate: [AuthGuard],
      },
      // 404 and Wildcard
      {
        path: 'not-found',
        loadChildren: () =>
          import('@features/not-found/not-found.module').then(
            m => m.NotFoundModule,
          ),
      },

      // Unverified
      {
        path: 'unverified',
        loadChildren: () =>
          import('@features/unverified/unverified.module').then(
            m => m.UnverifiedModule,
          ),
        canActivate: [RoleGuard],
        data: { roles: [Role.UNVERIFIED_NEIGHBOR] },
      },

      // Unverified
      {
        path: 'rejected',
        loadChildren: () =>
          import('@features/rejected/rejected.module').then(
            m => m.RejectedModule,
          ),
        canActivate: [RoleGuard],
        data: { roles: [Role.REJECTED] },
      },

      // Unverified
      {
        path: 'super-admin',
        loadChildren: () =>
          import('@features/super-admin/super-admin.module').then(
            m => m.SuperAdminModule,
          ),
        canActivate: [RoleGuard],
        data: { roles: [Role.SUPER_ADMIN] },
      },
    ],
  },

  {
    path: '',
    component: MainLayoutComponent,
    children: [
      // Admin
      {
        path: 'admin',
        loadChildren: () =>
          import('@features/admin/admin.module').then(m => m.AdminModule),
        canActivate: [RoleGuard],
        data: { roles: [Role.ADMINISTRATOR] },
      },

      // Amenities
      {
        path: 'amenities',
        loadChildren: () =>
          import('@features/amenities/amenities.module').then(
            m => m.AmenitiesModule,
          ),
        canActivate: [RoleGuard],
        data: { roles: [Role.NEIGHBOR, Role.ADMINISTRATOR] },
      },

      // Calendar
      {
        path: 'calendar',
        loadChildren: () =>
          import('@features/calendar/calendar.module').then(
            m => m.CalendarModule,
          ),
        canActivate: [RoleGuard],
        data: { roles: [Role.NEIGHBOR, Role.ADMINISTRATOR, Role.WORKER] },
      },

      // Feed
      {
        path: 'posts',
        loadChildren: () =>
          import('@features/feed/feed.module').then(m => m.FeedModule),
        canActivate: [RoleGuard],
        data: { roles: [Role.NEIGHBOR, Role.ADMINISTRATOR] },
      },

      // Information
      {
        path: 'information',
        loadChildren: () =>
          import('@features/information/information.module').then(
            m => m.InformationModule,
          ),
        canActivate: [RoleGuard],
        data: { roles: [Role.NEIGHBOR, Role.ADMINISTRATOR] },
      },

      // Marketplace
      {
        path: 'marketplace',
        loadChildren: () =>
          import('@features/marketplace/marketplace.module').then(
            m => m.MarketplaceModule,
          ),
        canActivate: [RoleGuard],
        data: { roles: [Role.NEIGHBOR, Role.ADMINISTRATOR] },
      },

      // Services
      {
        path: 'services',
        loadChildren: () =>
          import('@features/service-providers/service-providers.module').then(
            m => m.ServiceProvidersModule,
          ),
        canActivate: [RoleGuard],
        data: { roles: [Role.NEIGHBOR, Role.ADMINISTRATOR, Role.WORKER] },
      },

      // User Profile
      {
        path: 'profile',
        loadChildren: () =>
          import('@features/user-profile/user-profile.module').then(
            m => m.UserProfileModule,
          ),
        canActivate: [RoleGuard],
        data: { roles: [Role.NEIGHBOR, Role.ADMINISTRATOR, Role.WORKER] },
      },
    ],
  },

  { path: '**', redirectTo: '/not-found', pathMatch: 'full' },
];
