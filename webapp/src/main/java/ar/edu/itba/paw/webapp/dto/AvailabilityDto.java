package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Availability;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class AvailabilityDto {
    private URI self;
    private URI amenity; // localhost:8080/amenities/{id}
    private URI shift; // localhost:8080/shifts/{id}

    public static AvailabilityDto fromAvailability(Availability availability, UriInfo uriInfo){
        final AvailabilityDto dto = new AvailabilityDto();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("amenities")
                .path(String.valueOf(availability.getAmenity().getAmenityId()))
                .path("availability")
                .path(String.valueOf(availability.getAmenityAvailabilityId()))
                .build();
        dto.amenity = uriInfo.getBaseUriBuilder()
                .path("amenities")
                .path(String.valueOf(availability.getAmenity().getAmenityId()))
                .build();
        dto.shift = uriInfo.getBaseUriBuilder()
                .path("shifts")
                .path(String.valueOf(availability.getShift().getShiftId()))
                .build();

        return dto;
    }


    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getAmenity() {
        return amenity;
    }

    public void setAmenity(URI amenity) {
        this.amenity = amenity;
    }

    public URI getShift() {
        return shift;
    }

    public void setShift(URI shift) {
        this.shift = shift;
    }

}
