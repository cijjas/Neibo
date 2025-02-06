package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.ImagesURIConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.List;

public class ProductDto {

    @NotNull(groups = OnCreate.class)
    @Size(max = 100)
    private String name;

    @NotNull(groups = OnCreate.class)
    private Double price;

    @NotNull(groups = OnCreate.class)
    @NotEmpty(groups = OnCreate.class)
    @ImagesURIConstraint
    private List<String> images;

    @NotNull(groups = OnCreate.class)
    @Size(max = 2000)
    private String description;

    @NotNull(groups = OnCreate.class)
    private String department;

    @NotNull(groups = OnCreate.class)
    @Range(min = 1, max = 100)
    private Long remainingUnits;

    @NotNull(groups = OnCreate.class)
    private Boolean used;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
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

        String neighborhoodId = String.valueOf(product.getSeller().getNeighborhood().getNeighborhoodId());
        String productId = String.valueOf(product.getProductId());
        String userId = String.valueOf(product.getSeller().getUserId());
        String departmentId = String.valueOf(product.getDepartment().getDepartmentId());
        String requestStatusId = String.valueOf(RequestStatus.REQUESTED.getId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder departmentUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.DEPARTMENTS).path(departmentId);
        UriBuilder requestStatusUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.REQUEST_STATUSES).path(requestStatusId);
        UriBuilder productUri = neighborhoodUri.clone().path(Endpoint.PRODUCTS).path(productId);
        UriBuilder inquiriesUri = productUri.clone().path(Endpoint.INQUIRIES);
        UriBuilder requestsUri = neighborhoodUri.clone().path(Endpoint.REQUESTS).queryParam(QueryParameter.FOR_PRODUCT, productUri.build());
        UriBuilder pendingRequestsCountUri = neighborhoodUri.clone().path(Endpoint.REQUESTS).path(Endpoint.COUNT)
                .queryParam(QueryParameter.FOR_PRODUCT, productUri.build())
                .queryParam(QueryParameter.WITH_STATUS, requestStatusUri.build());
        UriBuilder userUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USERS).path(userId);

        links.setSelf(productUri.build());
        links.setProductUser(userUri.build());
        links.setDepartment(departmentUri.build());
        links.setInquiries(inquiriesUri.build());
        links.setRequests(requestsUri.build());
        links.setPendingRequestsCount(pendingRequestsCountUri.build());

        if (product.getPrimaryPicture() != null) {
            String primaryPictureId = String.valueOf(product.getPrimaryPicture().getImageId());
            UriBuilder primaryPictureUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.IMAGES).path(primaryPictureId);
            links.setFirstProductImage(primaryPictureUri.build());
        }
        if (product.getSecondaryPicture() != null) {
            String secondaryPictureId = String.valueOf(product.getSecondaryPicture().getImageId());
            UriBuilder secondaryPictureUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.IMAGES).path(secondaryPictureId);
            links.setSecondProductImage(secondaryPictureUri.build());
        }
        if (product.getTertiaryPicture() != null) {
            String tertiaryPictureId = String.valueOf(product.getTertiaryPicture().getImageId());
            UriBuilder tertiaryPictureUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.IMAGES).path(tertiaryPictureId);
            links.setThirdProductImage(tertiaryPictureUri.build());
        }

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

    public void setUsed(boolean used) {
        this.used = used;
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
