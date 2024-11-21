package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.webapp.validation.constraints.form.ShiftsURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ShiftsURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Form;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.Reference;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class AmenityDto {

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 100, groups = Basic.class)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*", groups = Basic.class)
    private String name;

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 1000, groups = Basic.class)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*", groups = Basic.class)
    private String description;

    @ShiftsURNFormConstraint(groups = Form.class)
    @ShiftsURNReferenceConstraint(groups = Reference.class)
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
}
