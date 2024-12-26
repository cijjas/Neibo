package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.EventURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;

public class AttendanceDto {

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNCreateReferenceConstraint(groups = Authorization.class)
    private String user;

    @NotNull(groups = Null.class)
    @EventURNConstraint(groups = URN.class)
    private String event;

    private Links _links;

    public static AttendanceDto fromAttendance(Attendance attendance, UriInfo uriInfo) {
        final AttendanceDto dto = new AttendanceDto();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                .path("attendance")
                .queryParam("forEvent",
                        uriInfo.getBaseUriBuilder()
                                .path("neighborhoods")
                                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                                .path("events")
                                .path(String.valueOf(attendance.getEvent().getEventId()))
                                .build())
                .queryParam("forUser",
                        uriInfo.getBaseUriBuilder()
                                .path("neighborhoods")
                                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                                .path("users")
                                .path(String.valueOf(attendance.getUser().getUserId()))
                                .build())
                .build());
        links.setAttendanceUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(attendance.getUser().getUserId()))
                .build());
        links.setEvent(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                .path("events")
                .path(String.valueOf(attendance.getEvent().getEventId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

}
