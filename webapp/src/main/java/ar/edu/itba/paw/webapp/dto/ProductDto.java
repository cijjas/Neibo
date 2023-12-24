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
                .path("products")
                .path(String.valueOf(product.getProductId()))
                .build();
        dto.primaryPicture = uriInfo.getBaseUriBuilder()
                .path("images")
                .path(String.valueOf(product.getPrimaryPicture().getImageId()))
                .build();
        dto.secondaryPicture = uriInfo.getBaseUriBuilder()
                .path("images")
                .path(String.valueOf(product.getSecondaryPicture().getImageId()))
                .build();
        dto.tertiaryPicture = uriInfo.getBaseUriBuilder()
                .path("images")
                .path(String.valueOf(product.getTertiaryPicture().getImageId()))
                .build();
        dto.seller = uriInfo.getBaseUriBuilder()
                .path("users")
                .path(String.valueOf(product.getSeller().getUserId()))
                .build();
        dto.department = uriInfo.getBaseUriBuilder()
                .path("departments")
                .path(String.valueOf(product.getDepartment().getDepartmentId()))
                .build();
        dto.inquiries = uriInfo.getBaseUriBuilder()
                .path("products")
                .path(String.valueOf(product.getProductId()))
                .path("inquiries")
                .build();
        dto.requests = uriInfo.getBaseUriBuilder()
                .path("products")
                .path(String.valueOf(product.getProductId()))
                .path("requests")
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

}
