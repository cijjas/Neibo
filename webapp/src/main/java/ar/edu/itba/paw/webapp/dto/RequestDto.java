package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.ProductURNInRequestConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProductURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.RequestStatusURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class RequestDto {

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 500, groups = Basic.class)
    private String message;

    @NotNull(groups = Null.class)
    @ProductURNConstraint(groups = URN.class)
    @ProductURNInRequestConstraint(groups = Authorization.class)
    private String product;

    @NotNull(groups = Null.class)
    @Range(min = 1, max = 100, groups = Basic.class)
    private Integer unitsRequested;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNCreateReferenceConstraint(groups = Authorization.class)
    private String user;

    @RequestStatusURNConstraint(groups = URN.class)
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
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(request.getUser().getNeighborhood().getNeighborhoodId()))
                .path("requests")
                .path(String.valueOf(request.getRequestId()))
                .build());
        links.setRequestStatus(uriInfo.getBaseUriBuilder()
                .path("request-statuses")
                .path(String.valueOf(request.getStatus().getId()))
                .build());
        links.setProduct(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(request.getUser().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(request.getProduct().getProductId()))
                .build());
        links.setCommentUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(request.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(request.getUser().getUserId()))
                .build());
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
