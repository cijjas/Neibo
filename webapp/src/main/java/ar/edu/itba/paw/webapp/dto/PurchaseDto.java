package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Purchase;

import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class PurchaseDto {

    private Long units;
    private Date purchaseDate;
    private Links _links;

    public static PurchaseDto fromPurchase(Purchase purchase, UriInfo uriInfo) {
        final PurchaseDto dto = new PurchaseDto();

        dto.units = purchase.getUnits();
        dto.purchaseDate = purchase.getPurchaseDate();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(purchase.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(purchase.getUser().getUserId()))
                .path("transactions")
                .path(String.valueOf(purchase.getPurchaseId()))
                .build());
        links.setProduct(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(purchase.getUser().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(purchase.getProduct().getProductId()))
                .build());
        links.setUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(purchase.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(purchase.getUser().getUserId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public Long getUnits() {
        return units;
    }

    public void setUnits(Long units) {
        this.units = units;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
