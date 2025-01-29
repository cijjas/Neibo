import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  MarketplacePageComponent,
  MarketplaceDashboardBuyerPageComponent,
  MarketplaceDashboardSellerPageComponent,
  MarketplaceProductSellPageComponent,
  MarketplaceProductDetailPageComponent,
  MarketplaceProductEditPageComponent,
  MarketplaceProductRequestsPageComponent,
} from '@features/index';
import { productResolver } from '@shared/resolvers/product.resolver';

const routes: Routes = [
  { path: '', component: MarketplacePageComponent },
  {
    path: 'buyer-hub/:mode',
    component: MarketplaceDashboardBuyerPageComponent,
  },
  {
    path: 'seller-hub/:mode',
    component: MarketplaceDashboardSellerPageComponent,
  },
  { path: 'products/new', component: MarketplaceProductSellPageComponent },
  {
    path: 'products/:id',
    component: MarketplaceProductDetailPageComponent,
    resolve: { product: productResolver },
  },
  {
    path: 'products/:id/edit',
    component: MarketplaceProductEditPageComponent,
    resolve: { product: productResolver },
  },
  {
    path: 'products/:id/requests',
    component: MarketplaceProductRequestsPageComponent,
    resolve: { product: productResolver },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MarketplaceRoutingModule {}
