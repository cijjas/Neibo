package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.ProductStatus;

import javax.ws.rs.core.UriInfo;

public class ProductStatusDto {

    private ProductStatus productStatus;

    private Links _links;

    public static ProductStatusDto fromProductStatus(ProductStatus productStatus, UriInfo uriInfo) {
        final ProductStatusDto dto = new ProductStatusDto();

        dto.productStatus = productStatus;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("product-statuses")
                .path(String.valueOf(productStatus.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
