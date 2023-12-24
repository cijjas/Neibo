package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Resource;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ResourceDto {

    private String title;
    private String description;
    private URI self;
    private URI image;
    private URI neighborhood;

    public static ResourceDto fromResource(Resource resource, UriInfo uriInfo){
        final ResourceDto dto = new ResourceDto();

        dto.title = resource.getTitle();
        dto.description = resource.getDescription();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("resources")
                .path(String.valueOf(resource.getResourceId()))
                .build();
        dto.image = uriInfo.getBaseUriBuilder()
                .path("resources")
                .path(String.valueOf(resource.getResourceId()))
                .path("image")
                .build();
        dto.neighborhood = uriInfo.getBaseUriBuilder()
                .path("neighborhood")
                .path(String.valueOf(resource.getNeighborhood().getNeighborhoodId()))
                .build();

        return dto;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

}
