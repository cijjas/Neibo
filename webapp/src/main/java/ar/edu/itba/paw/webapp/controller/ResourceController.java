package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ResourceService;
import ar.edu.itba.paw.models.Entities.Resource;
import ar.edu.itba.paw.webapp.controller.constants.*;
import ar.edu.itba.paw.webapp.dto.ResourceDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalFirstId;

/*
 * # Summary
 *   - A Neighborhood has many Resources, like a map, the emblem, etc
 *
 * # Use cases
 *   - An Admin can create Resources in their Neighborhood
 *   - A User/Admin can list the Resources of their Neighborhood
 */

@Path(Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.RESOURCES)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON})
public class ResourceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);
    private final ResourceService rs;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public ResourceController(ResourceService rs) {
        this.rs = rs;
    }

    @GET
    public Response listResources(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/resources'", neighborhoodId);

        // Content
        final List<Resource> resources = rs.getResources(neighborhoodId, page, size);
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

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.RESOURCES),
                rs.calculateResourcePages(neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<ResourceDto>>(resourcesDto) {
                })
                .cacheControl(cacheControl)
                .tag(resourcesHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("{" + PathParameter.RESOURCE_ID + "}")
    public Response findResource(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.RESOURCE_ID) @GenericIdConstraint long resourceId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, resourceId);

        // Content
        Resource resource = rs.findResource(neighborhoodId, resourceId).orElseThrow(NotFoundException::new);
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
    @Secured({UserRole.ADMINISTRATOR, UserRole.SUPER_ADMINISTRATOR})
    @Validated(CreateValidationSequence.class)
    public Response createResource(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @Valid @NotNull ResourceDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/resources'", neighborhoodId);

        // Creation & ETag Generation
        final Resource resource = rs.createResource(neighborhoodId, createForm.getTitle(), createForm.getDescription(), extractOptionalFirstId(createForm.getImage()));
        String resourceHashCode = String.valueOf(resource.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(resource.getResourceId())).build();

        return Response.created(uri)
                .tag(resourceHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.RESOURCE_ID + "}")
    @Secured({UserRole.ADMINISTRATOR, UserRole.SUPER_ADMINISTRATOR})
    @Validated(UpdateValidationSequence.class)
    public Response updateResource(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.RESOURCE_ID) @GenericIdConstraint long resourceId,
            @Valid @NotNull ResourceDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, resourceId);

        // Modification & HashCode Generation
        final Resource updatedResource = rs.updateResource(neighborhoodId, resourceId, updateForm.getTitle(), updateForm.getDescription(), extractOptionalFirstId(updateForm.getImage()));
        String resourceHashCode = String.valueOf(updatedResource.hashCode());

        return Response.ok(ResourceDto.fromResource(updatedResource, uriInfo))
                .tag(resourceHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.RESOURCE_ID + "}")
    @Secured({UserRole.ADMINISTRATOR, UserRole.SUPER_ADMINISTRATOR})
    public Response deleteResource(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.RESOURCE_ID) @GenericIdConstraint long resourceId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, resourceId);

        // Deletion Attempt
        if (rs.deleteResource(neighborhoodId, resourceId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
