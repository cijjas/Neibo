import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
    MarketplaceControlBarComponent,
    MarketplaceDashboardBuyerPageComponent,
    MarketplaceDashboardSellerPageComponent,
    MarketplacePageComponent,
    MarketplaceProductDetailPageComponent,
    MarketplaceProductEditPageComponent,
    MarketplaceProductPreviewComponent,
    MarketplaceProductRequestsPageComponent,
    MarketplaceProductSellPageComponent
} from '@features/index';

import { SharedModule } from '@shared/shared.module';
import { MarketplaceRoutingModule } from './marketplace-routing.module';

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
        MarketplaceProductSellPageComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        MarketplaceRoutingModule
    ],
    exports: [
        MarketplaceControlBarComponent,
        MarketplaceDashboardBuyerPageComponent,
        MarketplaceDashboardSellerPageComponent,
        MarketplacePageComponent,
        MarketplaceProductDetailPageComponent,
        MarketplaceProductEditPageComponent,
        MarketplaceProductPreviewComponent,
        MarketplaceProductRequestsPageComponent,
        MarketplaceProductSellPageComponent
    ]
})
export class MarketplaceModule { }
