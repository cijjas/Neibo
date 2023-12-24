package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Event;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class EventDto {
    private String name;
    private String description;
    private Date date;
    private URI self;
    private URI neighborhood; // localhost:8080/neighborhood/{id}
    private URI startTime; // localhost:8080/neighborhood/{id}
    private URI endTime; // localhost:8080/neighborhood/{id}
    private URI attendees; // localhost:8080/amenities/{id}/availability

    public static EventDto fromEvent(Event event, UriInfo uriInfo){
        final EventDto dto = new EventDto();

        dto.name = event.getName();
        dto.description = event.getDescription();
        dto.date = event.getDate();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(event.getNeighborhood().getNeighborhoodId()))
                .path("events")
                .path(String.valueOf(event.getEventId()))
                .build();
        dto.neighborhood = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(event.getNeighborhood().getNeighborhoodId()))
                .build();
        dto.startTime = uriInfo.getBaseUriBuilder()
                .path("times")
                .path(String.valueOf(event.getStartTime().getTimeId()))
                .build();
        dto.endTime = uriInfo.getBaseUriBuilder()
                .path("times")
                .path(String.valueOf(event.getEndTime().getTimeId()))
                .build();
        dto.attendees = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(event.getNeighborhood().getNeighborhoodId()))
                .path("events")
                .path(String.valueOf(event.getEventId()))
                .path("attendees")
                .build();

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

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(URI neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public URI getStartTime() {
        return startTime;
    }

    public void setStartTime(URI startTime) {
        this.startTime = startTime;
    }

    public URI getEndTime() {
        return endTime;
    }

    public void setEndTime(URI endTime) {
        this.endTime = endTime;
    }

    public URI getAttendees() {
        return attendees;
    }

    public void setAttendees(URI attendees) {
        this.attendees = attendees;
    }
}
