import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MarketplacePageComponent } from './marketplace-page/marketplace-page.component';
import { MarketplaceDashboardBuyerPageComponent } from './marketplace-dashboard-buyer-page/marketplace-dashboard-buyer-page.component';
import { MarketplaceDashboardSellerPageComponent } from './marketplace-dashboard-seller-page/marketplace-dashboard-seller-page.component';
import { MarketplaceProductSellPageComponent } from './marketplace-product-sell-page/marketplace-product-sell-page.component';
import { MarketplaceProductDetailPageComponent } from './marketplace-product-detail-page/marketplace-product-detail-page.component';
import { MarketplaceProductEditPageComponent } from './marketplace-product-edit-page/marketplace-product-edit-page.component';
import { MarketplaceProductRequestsPageComponent } from './marketplace-product-requests-page/marketplace-product-requests-page.component';
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
