package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Amenity;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class AmenityDto {

    private String name;
    private String description;
    private Links _links;

    public static AmenityDto fromAmenity(Amenity amenity, UriInfo uriInfo) {
        final AmenityDto dto = new AmenityDto();

        dto.name = amenity.getName();
        dto.description = amenity.getDescription();

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(amenity.getNeighborhood().getNeighborhoodId()))
                .path("amenities")
                .path(String.valueOf(amenity.getAmenityId()))
                .build();
        links.setSelf(self);
        links.setNeighborhood(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(amenity.getNeighborhood().getNeighborhoodId()))
                .build());
        links.setShifts(uriInfo.getBaseUriBuilder()
                .path("shifts")
                .queryParam("forAmenity", self)
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

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
