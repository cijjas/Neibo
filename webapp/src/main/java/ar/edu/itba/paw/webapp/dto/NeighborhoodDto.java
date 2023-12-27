package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Neighborhood;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class NeighborhoodDto {
    private String name;
    private URI self;
    private URI users;
    private URI workers;
    private URI contacts; // localhost:8080/neighborhood/{id}/contacts
    private URI channels; // localhost:8080/neighborhood/{id}/channels
    private URI events; // localhost:8080/neighborhood/{id}/events
    private URI resources; // localhost:8080/neighborhood/{id}/resources

    public static NeighborhoodDto fromNeighborhood(Neighborhood neighborhood, UriInfo uriInfo){
        final NeighborhoodDto dto = new NeighborhoodDto();

        dto.name = neighborhood.getName();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .build();
        dto.users = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .path("users")
                .build();
        dto.workers = uriInfo.getBaseUriBuilder()
                .path("workers")
                .queryParam("neighborhood", String.valueOf(neighborhood.getNeighborhoodId()))
                .build();
        dto.contacts = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .path("contacts")
                .build();
        dto.channels = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .path("channels")
                .build();
        dto.events = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .path("events")
                .build();
        dto.resources = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .path("resources")
                .build();

        return dto;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

}
