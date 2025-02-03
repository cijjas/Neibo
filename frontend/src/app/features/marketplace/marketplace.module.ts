import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { MarketplaceRoutingModule } from './marketplace-routing.module';

import { MarketplaceControlBarComponent } from './marketplace-control-bar/marketplace-control-bar.component';
import { MarketplaceProductPreviewComponent } from './marketplace-product-preview/marketplace-product-preview.component';
import { MarketplacePageComponent } from './marketplace-page/marketplace-page.component';
import { MarketplaceDashboardBuyerPageComponent } from './marketplace-dashboard-buyer-page/marketplace-dashboard-buyer-page.component';
import { MarketplaceDashboardSellerPageComponent } from './marketplace-dashboard-seller-page/marketplace-dashboard-seller-page.component';
import { MarketplaceProductSellPageComponent } from './marketplace-product-sell-page/marketplace-product-sell-page.component';
import { MarketplaceProductDetailPageComponent } from './marketplace-product-detail-page/marketplace-product-detail-page.component';
import { MarketplaceProductEditPageComponent } from './marketplace-product-edit-page/marketplace-product-edit-page.component';
import { MarketplaceProductRequestsPageComponent } from './marketplace-product-requests-page/marketplace-product-requests-page.component';

@NgModule({
  declarations: [
    MarketplaceControlBarComponent,
    MarketplaceDashboardBuyerPageComponent,
    MarketplaceDashboardSellerPageComponent,
    MarketplacePageComponent,
    MarketplaceProductDetailPageComponent,
    MarketplaceProductEditPageComponent,
    MarketplaceProductPreviewComponent,
    MarketplaceProductRequestsPageComponent,
    MarketplaceProductSellPageComponent,
  ],
  imports: [CommonModule, SharedModule, MarketplaceRoutingModule],
  exports: [
    MarketplaceControlBarComponent,
    MarketplaceDashboardBuyerPageComponent,
    MarketplaceDashboardSellerPageComponent,
    MarketplacePageComponent,
    MarketplaceProductDetailPageComponent,
    MarketplaceProductEditPageComponent,
    MarketplaceProductPreviewComponent,
    MarketplaceProductRequestsPageComponent,
    MarketplaceProductSellPageComponent,
  ],
})
export class MarketplaceModule {}
