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

        UriBuilder attendanceCountUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString()).path(neighborhoodId).path(Endpoint.ATTENDANCE.toString()).path(Endpoint.COUNT.toString());
        UriBuilder self;

        if (eventURI != null && userURI != null) {
            self = attendanceCountUri.clone().queryParam(QueryParameters.FOR_USER, userURI).queryParam(QueryParameters.FOR_EVENT, eventURI);
        } else if (eventURI != null) {
            self = attendanceCountUri.clone().queryParam(QueryParameters.FOR_EVENT, eventURI);
        } else if (userURI != null) {
            self = attendanceCountUri.clone().queryParam(QueryParameters.FOR_USER, userURI);
        } else {
            self = attendanceCountUri;
        }

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
