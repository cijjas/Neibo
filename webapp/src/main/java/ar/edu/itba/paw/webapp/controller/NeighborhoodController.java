package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.dto.NeighborhoodDto;
import ar.edu.itba.paw.webapp.form.NewNeighborhoodForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods")
@Component
public class NeighborhoodController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodController.class);

    @Autowired
    private NeighborhoodService ns;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.hasAccessNeighborhoodQP(#workerId)")
    public Response listNeighborhoods(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("withWorker") final Long workerId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Neighborhood> neighborhoods = ns.getNeighborhoods(page, size, workerId);
        if (neighborhoods.isEmpty())
            return Response.noContent().build();
        final List<NeighborhoodDto> neighborhoodsDto = neighborhoods.stream()
                .map(n -> NeighborhoodDto.fromNeighborhood(n, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString(),
                ns.calculateNeighborhoodPages(workerId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<NeighborhoodDto>>(neighborhoodsDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findNeighborhood(
            @PathParam("id") final long id
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}'", id);
        Neighborhood neighborhood = ns.findNeighborhood(id).orElseThrow(NotFoundException::new);
        // Use stored ETag value
        EntityTag entityTag = new EntityTag(neighborhood.getVersion().toString());
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        // Client has a valid version
        if (builder != null)
            return builder.cacheControl(cacheControl).build();
        // Client has an invalid version
        return Response.ok(NeighborhoodDto.fromNeighborhood(neighborhood, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createNeighborhood(
            @Valid final NewNeighborhoodForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/'");

        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .entity("Your cached version of the resource is outdated.")
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        final Neighborhood neighborhood = ns.createNeighborhood(form.getName());
        entityLevelETag = ETagUtility.generateETag();
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(neighborhood.getNeighborhoodId())).build();
        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }
}
