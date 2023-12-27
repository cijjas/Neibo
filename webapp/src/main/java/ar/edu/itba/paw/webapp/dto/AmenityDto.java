package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Amenity;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class AmenityDto {

    private String name;
    private String description;
    private URI self;
    private URI neighborhood; // localhost:8080/neighborhood/{id}
    private URI availability; // localhost:8080/amenities/{id}/availability

    public static AmenityDto fromAmenity(Amenity amenity, UriInfo uriInfo){
        final AmenityDto dto = new AmenityDto();

        dto.name = amenity.getName();
        dto.description = amenity.getDescription();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(amenity.getNeighborhood().getNeighborhoodId()))
                .path("amenities")
                .path(String.valueOf(amenity.getAmenityId()))
                .build();
        dto.neighborhood = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(amenity.getNeighborhood().getNeighborhoodId()))
                .build();
        dto.availability = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(amenity.getNeighborhood().getNeighborhoodId()))
                .path("amenities")
                .path(String.valueOf(amenity.getAmenityId()))
                .path("availability")
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

    public URI getAvailability() {
        return availability;
    }

    public void setAvailability(URI availability) {
        this.availability = availability;
    }
}
