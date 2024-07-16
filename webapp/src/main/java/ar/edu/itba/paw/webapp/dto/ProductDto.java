package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Product;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ProductDto {

    private String name;
    private String description;
    private Double price;
    private boolean used;
    private Long remainingUnits;
    private Links _links;

    public static ProductDto fromProduct(Product product, UriInfo uriInfo) {
        final ProductDto dto = new ProductDto();

        dto.name = product.getName();
        dto.description = product.getDescription();
        dto.price = product.getPrice();
        dto.used = product.isUsed();
        dto.remainingUnits = product.getRemainingUnits();

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(product.getSeller().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(product.getProductId()))
                .build();

        links.setSelf(self);
        if (product.getPrimaryPicture() != null) {
            links.setPrimaryPicture(uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(product.getPrimaryPicture().getImageId()))
                    .build());
        }
        if (product.getSecondaryPicture() != null) {
            links.setSecondaryPicture(uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(product.getSecondaryPicture().getImageId()))
                    .build());
        }
        if (product.getTertiaryPicture() != null) {
            links.setTertiaryPicture(uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(product.getTertiaryPicture().getImageId()))
                    .build());
        }


        links.setSeller(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(product.getSeller().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(product.getSeller().getUserId()))
                .build());
        links.setDepartment(uriInfo.getBaseUriBuilder()
                .path("departments")
                .path(String.valueOf(product.getDepartment().getDepartmentId()))
                .build());
        links.setInquiries(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(product.getSeller().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(product.getProductId()))
                .path("inquiries")
                .build());
        links.setRequests(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(product.getSeller().getNeighborhood().getNeighborhoodId()))
                .path("requests")
                .queryParam("forProduct", self)
                .build());
        dto.set_links(links);
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Long getRemainingUnits() {
        return remainingUnits;
    }

    public void setRemainingUnits(Long remainingUnits) {
        this.remainingUnits = remainingUnits;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
