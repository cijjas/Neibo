package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.constraints.ShiftsURIConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

public class AmenityDto {

    @NotNull(groups = OnCreate.class)
    @Size(min = 0, max = 100)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*")
    private String name;

    @NotNull(groups = OnCreate.class)
    @Size(min = 0, max = 1000)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*")
    private String description;

    @ShiftsURIConstraint
    private List<String> selectedShifts;

    private Links _links;

    public static AmenityDto fromAmenity(Amenity amenity, UriInfo uriInfo) {
        final AmenityDto dto = new AmenityDto();

        dto.name = amenity.getName();
        dto.description = amenity.getDescription();

        Links links = new Links();

        String neighborhoodId = String.valueOf(amenity.getNeighborhood().getNeighborhoodId());
        String amenityId = String.valueOf(amenity.getAmenityId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder amenityUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId).path(Endpoint.AMENITIES).path(amenityId);
        UriBuilder shiftsUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.SHIFTS).queryParam(QueryParameter.FOR_AMENITY, amenityUri.build());

        links.setSelf(amenityUri.build());
        links.setNeighborhood(neighborhoodUri.build());
        links.setShifts(shiftsUri.build());

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

    public List<String> getSelectedShifts() {
        return selectedShifts;
    }

    public void setSelectedShifts(List<String> selectedShifts) {
        this.selectedShifts = selectedShifts;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
