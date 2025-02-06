package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class AttendanceDto {

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.EVENT_URI_REGEX)
    private String event;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;

    private Links _links;

    public static AttendanceDto fromAttendance(Attendance attendance, UriInfo uriInfo) {
        final AttendanceDto dto = new AttendanceDto();

        Links links = new Links();

        String neighborhoodId = String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId());
        String eventId = String.valueOf(attendance.getEvent().getEventId());
        String userId = String.valueOf(attendance.getUser().getUserId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder eventUri = neighborhoodUri.clone().path(Endpoint.EVENTS).path(eventId);
        UriBuilder userUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USERS).path(userId);
        UriBuilder attendanceUri = neighborhoodUri.clone().path(Endpoint.ATTENDANCE)
                .queryParam(QueryParameter.FOR_EVENT, eventUri.build())
                .queryParam(QueryParameter.FOR_USER, userUri.build());

        links.setSelf(attendanceUri.build());
        links.setAttendanceUser(userUri.build());
        links.setEvent(eventUri.build());
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
