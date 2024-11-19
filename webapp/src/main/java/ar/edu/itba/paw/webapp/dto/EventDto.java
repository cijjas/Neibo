package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.webapp.validation.constraints.ReservationDateConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.ValidTimeRangeConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.OnUpdate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;

// @ValidTimeRangeConstraint
public class EventDto {

    @NotNull(groups = OnCreate.class)
    @Size(min = 0, max = 100, groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotNull(groups = OnCreate.class)
    @Size(min = 0, max = 2000, groups = {OnCreate.class, OnUpdate.class})
    private String description;

    @NotNull(groups = OnCreate.class)
    @ReservationDateConstraint(groups = {OnCreate.class, OnUpdate.class})
    private String date;

    @NotNull(groups = OnCreate.class)
    private String startTime;

    @NotNull(groups = OnCreate.class)
    private String endTime;

    private Links _links;

    public static EventDto fromEvent(Event event, UriInfo uriInfo) {
        final EventDto dto = new EventDto();

        dto.name = event.getName();
        dto.description = event.getDescription();
        dto.date = event.getDate().toString();
        dto.startTime = event.getStartTime().toString();
        dto.endTime = event.getEndTime().toString();

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
