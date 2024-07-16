package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Request;

import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class RequestDto {

    private String message;
    private Date requestDate;
    private Boolean fulfilled;
    private Links _links;

    public static RequestDto fromRequest(Request request, UriInfo uriInfo) {
        final RequestDto dto = new RequestDto();

        dto.message = request.getMessage();
        dto.requestDate = request.getRequestDate();
        dto.fulfilled = request.getFulfilled();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(request.getUser().getNeighborhood().getNeighborhoodId()))
                .path("requests")
                .path(String.valueOf(request.getRequestId()))
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

    public Boolean isFulfilled() {
        return fulfilled;
    }

    public Boolean getFulfilled() {
        return fulfilled;
    }

    public void setFulfilled(Boolean fulfilled) {
        this.fulfilled = fulfilled;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
