package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.UserURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;

public class AttendanceDto {

    private Links _links;

    @NotNull(groups = OnCreate.class)
    @UserURNFormConstraint(groups = OnCreate.class)
    @UserURNReferenceConstraint(groups = OnCreate.class)
    @UserURNCreateReferenceConstraint(groups = OnCreate.class)
    private String user;

    public static AttendanceDto fromAttendance(Attendance attendance, UriInfo uriInfo) {
        final AttendanceDto dto = new AttendanceDto();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                .path("events")
                .path(String.valueOf(attendance.getEvent().getEventId()))
                .path("attendance")
                .path(String.valueOf(attendance.getId().getUserId()))
                .build());
        links.setUser(uriInfo.getBaseUriBuilder()
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

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

}
