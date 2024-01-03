package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ResourceService;
import ar.edu.itba.paw.models.Entities.Resource;
import ar.edu.itba.paw.webapp.dto.ResourceDto;
import ar.edu.itba.paw.webapp.form.ResourceForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Path("neighborhoods/{neighborhoodId}/resources")
@Component
public class ResourceController {

    @Autowired
    private ResourceService rs;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listResources() {
        final List<Resource> resources = rs.getResources(neighborhoodId);
        final List<ResourceDto> resourcesDto = resources.stream()
                .map(r -> ResourceDto.fromResource(r, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/resources";

        return Response.ok(new GenericEntity<List<ResourceDto>>(resourcesDto){}).build();

    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createResource(@Valid final ResourceForm form) {
        final Resource resource = rs.createResource(neighborhoodId, form.getTitle(), form.getDescription(), form.getImageFile());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(resource.getResourceId())).build();
        return Response.created(uri).build();
    }
    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        rs.deleteResource(id);
        return Response.noContent().build();
    }

}
