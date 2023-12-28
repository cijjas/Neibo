package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Request;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class RequestDto {

    private String message;
    private Date requestDate;
    private Boolean fulfilled;
    private URI self;
    private URI product;
    private URI user;

    public static RequestDto fromRequest(Request request, UriInfo uriInfo){
        final RequestDto dto = new RequestDto();

        dto.message = request.getMessage();
        dto.requestDate = request.getRequestDate();
        dto.fulfilled = request.getFulfilled();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(request.getUser().getNeighborhood().getNeighborhoodId()))
                .path("requests")
                .path(String.valueOf(request.getRequestId()))
                .build();
        dto.product = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(request.getUser().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(request.getProduct().getProductId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(request.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(request.getUser().getUserId()))
                .build();

        return dto;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
    public Boolean isFulfilled() { return fulfilled; }
    public void setFulfilled(Boolean fulfilled) { this.fulfilled = fulfilled; }

    public Boolean getFulfilled() {
        return fulfilled;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getProduct() {
        return product;
    }

    public void setProduct(URI product) {
        this.product = product;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }
}
