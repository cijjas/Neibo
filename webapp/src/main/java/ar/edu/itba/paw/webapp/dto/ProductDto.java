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
    private URI self;
    private URI primaryPicture;
    private URI secondaryPicture;
    private URI tertiaryPicture;
    private URI seller;
    private URI department;
    private URI inquiries;
    private URI requests;
    // private URI purchases; ?

    public static ProductDto fromProduct(Product product, UriInfo uriInfo){
        final ProductDto dto = new ProductDto();

        dto.name = product.getName();
        dto.description = product.getDescription();
        dto.price = product.getPrice();
        dto.used = product.isUsed();
        dto.remainingUnits = product.getRemainingUnits();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(product.getSeller().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(product.getProductId()))
                .build();
        if ( product.getPrimaryPicture() != null ){
            dto.primaryPicture = uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(product.getPrimaryPicture().getImageId()))
                    .build();
        }
        if ( product.getSecondaryPicture() != null ) {
            dto.secondaryPicture = uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(product.getSecondaryPicture().getImageId()))
                    .build();
        }
        if (product.getTertiaryPicture() != null) {
            dto.tertiaryPicture = uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(product.getTertiaryPicture().getImageId()))
                    .build();
        }
        dto.seller = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(product.getSeller().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(product.getSeller().getUserId()))
                .build();

        dto.department = uriInfo.getBaseUriBuilder()
                .path("departments")
                .path(String.valueOf(product.getDepartment().getDepartmentId()))
                .build();
        dto.inquiries = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(product.getSeller().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(product.getProductId()))
                .path("inquiries")
                .build();
        dto.requests = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(product.getSeller().getNeighborhood().getNeighborhoodId()))
                .path("requests")
                .queryParam("productId", product.getProductId())
                .build();

        return dto;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
    public Long getRemainingUnits() { return remainingUnits; }
    public void setRemainingUnits(Long remainingUnits) { this.remainingUnits = remainingUnits; }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getPrimaryPicture() {
        return primaryPicture;
    }

    public void setPrimaryPicture(URI primaryPicture) {
        this.primaryPicture = primaryPicture;
    }

    public URI getSecondaryPicture() {
        return secondaryPicture;
    }

    public void setSecondaryPicture(URI secondaryPicture) {
        this.secondaryPicture = secondaryPicture;
    }

    public URI getTertiaryPicture() {
        return tertiaryPicture;
    }

    public void setTertiaryPicture(URI tertiaryPicture) {
        this.tertiaryPicture = tertiaryPicture;
    }

    public URI getSeller() {
        return seller;
    }

    public void setSeller(URI seller) {
        this.seller = seller;
    }

    public URI getDepartment() {
        return department;
    }

    public void setDepartment(URI department) {
        this.department = department;
    }

    public URI getInquiries() {
        return inquiries;
    }

    public void setInquiries(URI inquiries) {
        this.inquiries = inquiries;
    }

    public URI getRequests() {
        return requests;
    }

    public void setRequests(URI requests) {
        this.requests = requests;
    }
}
