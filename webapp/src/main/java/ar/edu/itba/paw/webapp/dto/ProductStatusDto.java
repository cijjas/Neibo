package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.ProductStatus;

import javax.ws.rs.core.UriInfo;

public class ProductStatusDto {

    private ProductStatus status;

    private Links _links;

    public static ProductStatusDto fromProductStatus(ProductStatus productStatus, UriInfo uriInfo) {
        final ProductStatusDto dto = new ProductStatusDto();

        dto.status = productStatus;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("product-statuses")
                .path(String.valueOf(productStatus.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
