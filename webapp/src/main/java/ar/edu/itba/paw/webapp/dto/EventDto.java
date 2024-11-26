package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.webapp.validation.constraints.specific.DateConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.TimeConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.TimeRangeConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.Specific;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.time.format.DateTimeFormatter;

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
    private String date;

    @NotNull(groups = Null.class)
    @TimeConstraint
    private String startTime;

    @TimeConstraint
    @NotNull(groups = Null.class)
    private String endTime;

    private Links _links;

    public static EventDto fromEvent(Event event, UriInfo uriInfo) {
        final EventDto dto = new EventDto();

        dto.name = event.getName();
        dto.description = event.getDescription();
        dto.date = event.getDate().toString();
        dto.startTime = event.getStartTime().getTimeInterval().toString();
        dto.endTime = event.getEndTime().getTimeInterval().toString();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(event.getNeighborhood().getNeighborhoodId()))
                .path("events")
                .path(String.valueOf(event.getEventId()))
                .build());
        links.setNeighborhood(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(event.getNeighborhood().getNeighborhoodId()))
                .build());
        links.setAttendees(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(event.getNeighborhood().getNeighborhoodId()))
                .path("events")
                .path(String.valueOf(event.getEventId()))
                .path("attendees")
                .build());
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
