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
                .path("requests")
                .path(String.valueOf(request.getRequestId()))
                .build();
        dto.product = uriInfo.getBaseUriBuilder()
                .path("products")
                .path(String.valueOf(request.getProduct().getProductId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
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

}
