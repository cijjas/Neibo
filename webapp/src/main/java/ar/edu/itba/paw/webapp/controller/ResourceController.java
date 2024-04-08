package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ResourceService;
import ar.edu.itba.paw.models.Entities.Resource;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.ResourceDto;
import ar.edu.itba.paw.webapp.form.ResourceForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkModificationETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkMutableETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.CUSTOM_ROW_LEVEL_ETAG_NAME;

@Path("neighborhoods/{neighborhoodId}/resources")
@Component
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

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listResources() {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/resources'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Resource> resources = rs.getResources(neighborhoodId);
        if (resources.isEmpty())
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        final List<ResourceDto> resourcesDto = resources.stream()
                .map(r -> ResourceDto.fromResource(r, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ResourceDto>>(resourcesDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findResource(
            @PathParam("id") final long resourceId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, resourceId);

        // Content
        Resource resource = rs.findResource(resourceId, neighborhoodId).orElseThrow(NotFoundException::new);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(resource.getVersion().toString());
        Response response = checkMutableETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        return Response.ok(ResourceDto.fromResource(resource, uriInfo))
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response createResource(
            @Valid @NotNull final ResourceForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/resources'", neighborhoodId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .tag(entityLevelETag)
                    .build();

        // Creation & ETag Generation
        final Resource resource = rs.createResource(neighborhoodId, form.getTitle(), form.getDescription(), form.getImageURN());
        entityLevelETag = ETagUtility.generateETag();
        EntityTag rowLevelETag = new EntityTag(resource.getVersion().toString());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(resource.getResourceId())).build();

        return Response.created(uri)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response updateResourcePartially(
            @PathParam("id") final long id,
            @Valid @NotNull final ResourceForm partialUpdate,
            @HeaderParam(HttpHeaders.IF_MATCH) EntityTag ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, id);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(rs.findResource(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString());
        Response response = checkModificationETagPreconditions(ifMatch, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Modification & ETag Generation
        final Resource updatedResource = rs.updateResource(id, partialUpdate.getTitle(), partialUpdate.getDescription(), partialUpdate.getImageURN());
        entityLevelETag = ETagUtility.generateETag();
        rowLevelETag = new EntityTag(updatedResource.getVersion().toString());

        return Response.ok(ResourceDto.fromResource(updatedResource, uriInfo))
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response deleteResourceById(
            @PathParam("id") final long id,
            @HeaderParam(HttpHeaders.IF_MATCH) EntityTag ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, id);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(rs.findResource(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString());
        Response response = checkModificationETagPreconditions(ifMatch, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Deletion & ETag Generation Attempt
        if(rs.deleteResource(id)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }
}
