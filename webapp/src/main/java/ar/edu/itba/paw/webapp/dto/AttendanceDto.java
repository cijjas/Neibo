package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.controller.QueryParameters;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceInCreationConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.EventURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class AttendanceDto {

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNReferenceInCreationConstraint(groups = Authorization.class)
    private String user;

    @NotNull(groups = Null.class)
    @EventURNConstraint(groups = URN.class)
    private String event;

    private Links _links;

    public static AttendanceDto fromAttendance(Attendance attendance, UriInfo uriInfo) {
        final AttendanceDto dto = new AttendanceDto();

        Links links = new Links();

        String neighborhoodId = String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()) ;
        String eventId = String.valueOf(attendance.getEvent().getEventId());
        String userId = String.valueOf(attendance.getUser().getUserId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString()).path(neighborhoodId);
        UriBuilder eventUri = neighborhoodUri.clone().path(Endpoint.EVENTS.toString()).path(eventId);
        UriBuilder userUri = neighborhoodUri.clone().path(Endpoint.USERS.toString()).path(userId);
        UriBuilder attendanceUri = neighborhoodUri.clone().path(Endpoint.ATTENDANCE.toString())
                .queryParam(QueryParameters.FOR_EVENT, eventUri.build())
                .queryParam(QueryParameters.FOR_USER, userUri.build());

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
