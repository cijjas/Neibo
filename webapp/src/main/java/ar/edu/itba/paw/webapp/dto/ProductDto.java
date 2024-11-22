package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceConstraintUpdate;
import ar.edu.itba.paw.webapp.validation.constraints.form.DepartmentURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.ImagesURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;
import java.util.List;

public class ProductDto {
    @NotNull(groups = Null.class)
    @Size(max = 100, groups = Basic.class)
    private String name;

    @NotNull(groups = Null.class)
    private Double price;

    @ImagesURNConstraint(groups = URN.class)
    private List<String> images;

    @NotNull(groups = Null.class)
    @Size(max = 2000, groups = Basic.class)
    private String description;

    @NotNull(groups = Null.class)
    @DepartmentURNConstraint(groups = URN.class)
    private String department;

    @NotNull(groups = Null.class)
    @Range(min = 1, max = 100, groups = Basic.class)
    private Long remainingUnits;

    @NotNull(groups = Null.class)
    private Boolean used;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    // Temporal fix until group sequences are resolved they are fixed and i dont what i should do hehehe
    @UserURNCreateReferenceConstraint(groups = Authorization.class)
    @UserURNReferenceConstraintUpdate(groups = Authorization.class)
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
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
