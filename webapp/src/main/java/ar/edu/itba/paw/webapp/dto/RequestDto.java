package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.ProductURNInRequestConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.RequestStatusURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProductURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ProductURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.RequestStatusURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.UserURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.OnUpdate;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class RequestDto {

    @NotNull(groups = OnCreate.class)
    @Size(min = 0, max = 500, groups = OnCreate.class)
    private String requestMessage;

    @NotNull(groups = OnCreate.class)
    @ProductURNFormConstraint(groups = OnCreate.class)
    @ProductURNReferenceConstraint(groups = OnCreate.class)
    @ProductURNInRequestConstraint(groups = OnCreate.class)
    private String product;

    @NotNull(groups = OnCreate.class)
    @Range(min = 1, max = 100, groups = OnCreate.class)
    private Integer units;

    @NotNull(groups = OnCreate.class)
    @UserURNFormConstraint(groups = OnCreate.class)
    @UserURNReferenceConstraint(groups = OnCreate.class)
    @UserURNCreateReferenceConstraint(groups = OnCreate.class)
    private String user;

    @RequestStatusURNFormConstraint(groups = OnUpdate.class)
    @RequestStatusURNReferenceConstraint(groups = OnUpdate.class)
    private String requestStatus;

    private Date requestDate;
    private Date purchaseDate;
    private Links _links;

    public static RequestDto fromRequest(Request request, UriInfo uriInfo) {
        final RequestDto dto = new RequestDto();

        dto.requestMessage = request.getMessage();
        dto.requestDate = request.getRequestDate();
        dto.units = request.getUnits();
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
        links.setUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(request.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(request.getUser().getUserId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
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

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
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
