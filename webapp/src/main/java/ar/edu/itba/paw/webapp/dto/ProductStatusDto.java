package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.ProductStatus;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ProductStatusDto {

    private ProductStatus productStatus;

    private URI self;

    public static ProductStatusDto fromProductStatus(ProductStatus productStatus, UriInfo uriInfo){
        final ProductStatusDto dto = new ProductStatusDto();

        dto.productStatus = productStatus;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("product-statuses")
                .path(String.valueOf(productStatus.getId()))
                .build();

        return dto;
    }

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
