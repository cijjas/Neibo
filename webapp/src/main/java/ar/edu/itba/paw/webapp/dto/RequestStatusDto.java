package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class RequestStatusDto {

    private RequestStatus status;

    private Links _links;

    public static RequestStatusDto fromRequestStatus(RequestStatus requestStatus, UriInfo uriInfo) {
        final RequestStatusDto dto = new RequestStatusDto();

        dto.status = requestStatus;

        Links links = new Links();

        String requestStatusId = String.valueOf(requestStatus.getId());

        UriBuilder requestStatusUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.REQUEST_STATUSES).path(requestStatusId);

        links.setSelf(requestStatusUri.build());

        dto.set_links(links);
        return dto;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
