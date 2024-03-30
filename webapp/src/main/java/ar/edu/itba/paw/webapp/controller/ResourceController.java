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
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
            return Response.noContent().build();
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
            @PathParam("id") final long resourceId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, resourceId);

        // Content
        Resource resource = rs.findResource(resourceId, neighborhoodId).orElseThrow(NotFoundException::new);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        EntityTag entityTag = new EntityTag(resource.getVersion().toString());
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ResourceDto.fromResource(resource, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response createResource(
            @Valid final ResourceForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/resources'", neighborhoodId);

        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .entity("Your cached version of the resource is outdated.")
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        final Resource resource = rs.createResource(neighborhoodId, form.getTitle(), form.getDescription(), form.getImageFile());
        entityLevelETag = ETagUtility.generateETag();
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(resource.getResourceId())).build();
        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response updateResourcePartially(
            @PathParam("id") final long id,
            @Valid final ResourceForm partialUpdate,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, id);

        // Check If-Match header
        if (ifMatch != null) {
            String version = rs.findResource(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(version));

            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, version)
                        .build();
        }

        final Resource resource = rs.updateResource(id, partialUpdate.getTitle(), partialUpdate.getDescription(), partialUpdate.getImageFile());
        entityLevelETag = ETagUtility.generateETag();
        return Response.ok(ResourceDto.fromResource(resource, uriInfo))
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response deleteResourceById(
            @PathParam("id") final long id,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/resources/{}'", neighborhoodId, id);

        if (ifMatch != null) {
            String version = rs.findResource(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(version));

            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, version)
                        .build();
        }

        if(rs.deleteResource(id)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
