package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Purchase;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class PurchaseDto {

    private Long units;
    private Date purchaseDate;
    private URI self;
    private URI product;
    private URI user;

    public static PurchaseDto fromPurchase(Purchase purchase, UriInfo uriInfo){
        final PurchaseDto dto = new PurchaseDto();

        dto.units = purchase.getUnits();
        dto.purchaseDate = purchase.getPurchaseDate();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(purchase.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(purchase.getUser().getUserId()))
                .path("purchases")
                .path(String.valueOf(purchase.getPurchaseId()))
                .build();
        dto.product = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(purchase.getUser().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(purchase.getProduct().getProductId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(purchase.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(purchase.getUser().getUserId()))
                .build();

        return dto;
    }

    public Long getUnits() { return units; }
    public void setUnits(Long units) { this.units = units; }
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }

}
