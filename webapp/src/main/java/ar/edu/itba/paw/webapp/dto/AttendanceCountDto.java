package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class AttendanceCountDto {

    private int count;

    private Links _links;

    public static AttendanceCountDto fromAttendanceCount(int attendeesCount, long eventId, long neighborhoodId, UriInfo uriInfo) {
        final AttendanceCountDto dto = new AttendanceCountDto();

        dto.count = attendeesCount;

        Links links = new Links();

        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("attendance")
                .path("count");

        links.setSelf(uriBuilder.build());

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
