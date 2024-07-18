package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.enums.TransactionType;

import javax.ws.rs.core.UriInfo;

public class RequestStatusDto {
    private RequestStatus requestStatus;
    private Links _links;

    public static RequestStatusDto fromRequestStatus(RequestStatus requestStatus, UriInfo uriInfo) {
        final RequestStatusDto dto = new RequestStatusDto();

        dto.requestStatus = requestStatus;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("request-statuses")
                .path(String.valueOf(requestStatus.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
