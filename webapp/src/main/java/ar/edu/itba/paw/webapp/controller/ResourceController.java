package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ResourceService;
import ar.edu.itba.paw.models.Entities.Resource;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.OnUpdate;
import ar.edu.itba.paw.webapp.dto.ResourceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/*
 * # Summary
 *   - A Neighborhood has many Resources, like a map, the emblem, etc
 *
 * # Use cases
 *   - An Admin can create Resources in their Neighborhood
 *   - A User/Admin can list the Resources of their Neighborhood
 */

@Path("neighborhoods/{neighborhoodId}/resources")
@Component
@Validated
public class ResourceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private ResourceService rs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;


    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response listResources() {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/resources'", neighborhoodId);

        // Content
        final List<Resource> resources = rs.getResources(neighborhoodId);
        String resourcesHashCode = String.valueOf(resources.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(resourcesHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (resources.isEmpty())
            return Response.noContent()
                    .tag(resourcesHashCode)
                    .build();

        final List<ResourceDto> resourcesDto = resources.stream()
                .map(r -> ResourceDto.fromResource(r, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ResourceDto>>(resourcesDto) {})
                .cacheControl(cacheControl)
                .tag(resourcesHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findResource(
            @PathParam("id") final long resourceId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, resourceId);

        // Content
        Resource resource = rs.findResource(resourceId, neighborhoodId).orElseThrow(NotFoundException::new);
        String resourceHashCode = String.valueOf(resource.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(resourceHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ResourceDto.fromResource(resource, uriInfo))
                .cacheControl(cacheControl)
                .tag(resourceHashCode)
                .build();
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Validated(OnCreate.class)
    public Response createResource(
            @Valid ResourceDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/resources'", neighborhoodId);

        // Creation & ETag Generation
        final Resource resource = rs.createResource(neighborhoodId, form.getTitle(), form.getDescription(), form.getImage());
        String resourceHashCode = String.valueOf(resource.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(resource.getResourceId())).build();

        return Response.created(uri)
                .tag(resourceHashCode)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Validated(OnUpdate.class)
    public Response updateResourcePartially(
            @PathParam("id") final long id,
            @Valid ResourceDto partialUpdate
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, id);

        // Modification & HashCode Generation
        final Resource updatedResource = rs.updateResource(id, partialUpdate.getTitle(), partialUpdate.getDescription(), partialUpdate.getImage());
        String resourceHashCode = String.valueOf(updatedResource.hashCode());

        return Response.ok(ResourceDto.fromResource(updatedResource, uriInfo))
                .tag(resourceHashCode)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    public Response deleteResourceById(
            @PathParam("id") final long id
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, id);

        // Deletion Attempt
        if (rs.deleteResource(id))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
