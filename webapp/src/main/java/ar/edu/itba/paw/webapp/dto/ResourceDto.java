package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Resource;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ImageURIConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URI;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ResourceDto {

    @Size(max = 1000, groups = Basic.class)
    private String description;

    @ImageURIConstraint(groups = URI.class)
    private String image;

    @NotNull(groups = Null.class)
    @Size(min = 1, max = 64, groups = Basic.class)
    private String title;

    private Links _links;

    public static ResourceDto fromResource(Resource resource, UriInfo uriInfo) {
        final ResourceDto dto = new ResourceDto();

        dto.title = resource.getTitle();
        dto.description = resource.getDescription();

        Links links = new Links();

        String neighborhoodId = String.valueOf(resource.getNeighborhood().getNeighborhoodId());
        String resourceId = String.valueOf(resource.getResourceId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder resourceUri = neighborhoodUri.clone().path(Endpoint.RESOURCES).path(resourceId);

        links.setSelf(resourceUri.build());
        links.setNeighborhood(neighborhoodUri.build());
        if (resource.getImage() != null) {
            String imageId = String.valueOf(resource.getImage().getImageId());
            UriBuilder imageUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.IMAGES).path(imageId);
            links.setResourceImage(imageUri.build());
        }

        dto.set_links(links);
        return dto;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
