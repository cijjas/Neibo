package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;
import ar.edu.itba.paw.webapp.controller.QueryParameters;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class AttendanceCountDto {

    private int count;

    private Links _links;

    public static AttendanceCountDto fromAttendanceCount(int attendeesCount, long neighborhoodIdLong, String eventURI, String userURI, UriInfo uriInfo) {
        final AttendanceCountDto dto = new AttendanceCountDto();

        dto.count = attendeesCount;

        Links links = new Links();

        String neighborhoodId = String.valueOf(neighborhoodIdLong);

        UriBuilder self = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString()).path(neighborhoodId).path(Endpoint.ATTENDANCE.toString()).path(Endpoint.COUNT.toString());

        if (eventURI != null)
           self.queryParam(QueryParameters.FOR_EVENT, eventURI);
        if (userURI != null)
            self.queryParam(QueryParameters.FOR_USER, userURI);

        links.setSelf(self.build());

        dto.set_links(links);
        return dto;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
