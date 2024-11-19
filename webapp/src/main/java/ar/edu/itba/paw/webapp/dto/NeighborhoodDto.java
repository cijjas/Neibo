package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class NeighborhoodDto {

    @NotNull(groups = OnCreate.class)
    @Size(min = 1, max = 20, groups = OnCreate.class)
    private String name;

    private Links _links;

    public static NeighborhoodDto fromNeighborhood(Neighborhood neighborhood, UriInfo uriInfo) {
        final NeighborhoodDto dto = new NeighborhoodDto();

        dto.name = neighborhood.getName();


        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .build();
        links.setSelf(self);
        links.setUsers(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .path("users")
                .build());
        links.setWorkers(uriInfo.getBaseUriBuilder()
                .path("workers")
                .queryParam("inNeighborhoods", self)
                .build());
        links.setChannels(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .path("channels")
                .build());

        if (neighborhood.getNeighborhoodId() > 0) {
            links.setContacts(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("contacts")
                    .build());
            links.setEvents(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("events")
                    .build());
            links.setResources(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("resources")
                    .build());
        }
        dto.set_links(links);
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
