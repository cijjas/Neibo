package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.validation.constraints.DepartmentURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.ImagesURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.OnUpdate;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class ProductDto {
    @NotNull(groups = OnCreate.class)
    @Size(max = 100, groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotNull(groups = OnCreate.class)
    private Double price;

    @ImagesURNConstraint(groups = {OnCreate.class, OnUpdate.class})
    private String[] images;

    @NotNull(groups = OnCreate.class)
    @Size(max = 2000, groups = {OnCreate.class, OnUpdate.class})
    private String description;

    @NotNull(groups = OnCreate.class)
    @DepartmentURNConstraint(groups = {OnCreate.class, OnUpdate.class})
    private String department;

    @NotNull(groups = OnCreate.class)
    @Range(min = 1, max = 100, groups = {OnCreate.class, OnUpdate.class})
    private Long remainingUnits;

    @NotNull(groups = OnCreate.class)
    private Boolean used;

    @NotNull(groups = OnCreate.class)
    @UserURNReferenceConstraint(groups = {OnCreate.class, OnUpdate.class})
    private String user;

    private Date creationDate;
    private Links _links;

    public static ProductDto fromProduct(Product product, UriInfo uriInfo) {
        final ProductDto dto = new ProductDto();

        dto.name = product.getName();
        dto.description = product.getDescription();
        dto.price = product.getPrice();
        dto.used = product.isUsed();
        dto.remainingUnits = product.getRemainingUnits();
        dto.creationDate = product.getCreationDate();

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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
