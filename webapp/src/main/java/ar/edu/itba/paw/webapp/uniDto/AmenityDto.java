package ar.edu.itba.paw.webapp.uniDto;

import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.webapp.form.validation.constraints.PostURNInLikeFormConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ShiftsURNConstraint;
import ar.edu.itba.paw.webapp.groups.Create;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Validated
public class AmenityDto {
    @NotNull()
    @Size(min = 0, max = 100)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*", groups = {Create.class})
    private String name;

    @NotNull()
    @Size(min = 0, max = 1000)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*", groups = {Create.class})
    private String description;

    @ShiftsURNConstraint()
    private List<String> selectedShifts;

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

    // Additional methods

    @Override
    public String toString() {
        return "AmenityForm{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", selectedShifts=" + selectedShifts +
                ", _links=" + _links +
                '}';
    }

    public static class Links {
        private URI self;
        private URI neighborhood;
        private URI shifts;

        // Getters and setters
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

        public URI getShifts() {
            return shifts;
        }

        public void setShifts(URI shifts) {
            this.shifts = shifts;
        }
    }
}
