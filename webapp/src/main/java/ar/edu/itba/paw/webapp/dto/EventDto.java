package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.webapp.controller.QueryParameters;
import ar.edu.itba.paw.webapp.validation.constraints.specific.DateConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.TimeConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.TimeRangeConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.Specific;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@TimeRangeConstraint
public class EventDto {

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 100, groups = Basic.class)
    private String name;

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 2000, groups = Basic.class)
    private String description;

    @NotNull(groups = Null.class)
    @DateConstraint(groups = Specific.class)
    private String eventDate;

    @NotNull(groups = Null.class)
    @TimeConstraint
    private String startTime;

    @TimeConstraint
    @NotNull(groups = Null.class)
    private String endTime;

    private Boolean willAttend;

    private Links _links;

    public static EventDto fromEvent(Event event, Boolean willAttend, UriInfo uriInfo) {
        final EventDto dto = new EventDto();

        dto.name = event.getName();
        dto.description = event.getDescription();
        dto.eventDate = event.getDate().toString();
        dto.startTime = event.getStartTime().getTimeInterval().toString();
        dto.endTime = event.getEndTime().getTimeInterval().toString();
        dto.willAttend = willAttend;

        Links links = new Links();

        String neighborhoodId = String.valueOf(event.getNeighborhood().getNeighborhoodId());
        String eventId = String.valueOf(event.getEventId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString()).path(neighborhoodId);
        UriBuilder eventUri = neighborhoodUri.clone().path(Endpoint.EVENTS.toString()).path(eventId);
        UriBuilder attendanceUri = neighborhoodUri.clone().path(Endpoint.ATTENDANCE.toString()).queryParam(QueryParameters.FOR_EVENT, eventUri);
        UriBuilder attendanceCountUri = neighborhoodUri.clone().path(Endpoint.ATTENDANCE.toString()).path(Endpoint.COUNT.toString()).queryParam(QueryParameters.FOR_EVENT, eventUri);

        links.setSelf(eventUri.build());
        links.setNeighborhood((neighborhoodUri.build()));
        links.setAttendanceUsers(attendanceUri.build());
        links.setAttendanceCount(attendanceCountUri.build());

        dto.set_links(links);
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public Boolean getWillAttend() {
        return willAttend;
    }

    public void setWillAttend(Boolean willAttend) {
        this.willAttend = willAttend;
    }
}
