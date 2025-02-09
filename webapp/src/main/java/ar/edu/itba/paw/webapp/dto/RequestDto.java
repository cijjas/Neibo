package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class RequestDto {

    @NotNull(groups = OnCreate.class)
    @Size(max = 512)
    private String message;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.PRODUCT_URI_REGEX)
    private String product;

    @NotNull(groups = OnCreate.class)
    private Integer unitsRequested;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;

    @Pattern(regexp = URIValidator.REQUEST_STATUS_URI_REGEX)
    private String requestStatus;

    private Date requestDate;

    private Date purchaseDate;

    private Links _links;

    public static RequestDto fromRequest(Request request, UriInfo uriInfo) {
        final RequestDto dto = new RequestDto();

        dto.message = request.getMessage();
        dto.unitsRequested = request.getUnits();
        dto.requestDate = request.getRequestDate();
        dto.purchaseDate = request.getPurchaseDate();

        Links links = new Links();

        String neighborhoodId = String.valueOf(request.getUser().getNeighborhood().getNeighborhoodId());
        String requestId = String.valueOf(request.getRequestId());
        String requestStatusId = String.valueOf(request.getStatus().getId());
        String productId = String.valueOf(request.getProduct().getProductId());
        String requestUserId = String.valueOf(request.getUser().getUserId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder requestStatusUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.REQUEST_STATUSES).path(requestStatusId);
        UriBuilder requestUri = neighborhoodUri.clone().path(Endpoint.REQUESTS).path(requestId);
        UriBuilder productUri = neighborhoodUri.clone().path(Endpoint.PRODUCTS).path(productId);
        UriBuilder requestUserUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USERS).path(requestUserId);

        links.setSelf(requestUri.build());
        links.setProduct(productUri.build());
        links.setRequestStatus(requestStatusUri.build());
        links.setRequestUser(requestUserUri.build());

        dto.set_links(links);
        return dto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getUnitsRequested() {
        return unitsRequested;
    }

    public void setUnitsRequested(Integer unitsRequested) {
        this.unitsRequested = unitsRequested;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
}
