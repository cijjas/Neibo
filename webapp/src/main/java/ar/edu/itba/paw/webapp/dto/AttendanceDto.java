package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Attendance;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class AttendanceDto {
    private URI self;
    private URI user; // localhost:8080/users/{id}
    private URI event; // localhost:8080/neighborhoods/{id}/events/{id}

    public static AttendanceDto fromAttendance(Attendance attendance, UriInfo uriInfo){
        final AttendanceDto dto = new AttendanceDto();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                .path("events")
                .path(String.valueOf(attendance.getEvent().getEventId()))
                .path("attendance")
                .path(String.valueOf(attendance.getId().getUserId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(attendance.getUser().getUserId()))
                .build();
        dto.event = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                .path("events")
                .path(String.valueOf(attendance.getEvent().getEventId()))
                .build();

        return dto;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public URI getEvent() {
        return event;
    }

    public void setEvent(URI event) {
        this.event = event;
    }

}
