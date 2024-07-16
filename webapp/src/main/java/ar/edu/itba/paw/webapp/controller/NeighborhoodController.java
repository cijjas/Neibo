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
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;

/*
 * # Summary
 *   - Main entity in the Application
 *   - Many Neighborhoods live under the same Database, so most entities have to have a reference to the Neighborhood they belong to
 *   - Has relationships with Users, Posts, Channels, Products and many more
 *
 * # Use cases
 *   - When registering all Neighborhoods have to be displayed
 */

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
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
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
            @PathParam("id") final long neighborhoodId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}'", neighborhoodId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(Long.toString(neighborhoodId));
        Response response = checkETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Content
        NeighborhoodDto neighborhoodDto = NeighborhoodDto.fromNeighborhood(ns.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new), uriInfo);

        return Response.ok(neighborhoodDto)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .header(HttpHeaders.CACHE_CONTROL, MAX_AGE_HEADER)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createNeighborhood(
            @Valid @NotNull final NewNeighborhoodForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/'");

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .tag(entityLevelETag)
                    .build();

        // Creation & ETag Generation
        final Neighborhood neighborhood = ns.createNeighborhood(form.getName());
        entityLevelETag = ETagUtility.generateETag();
        EntityTag rowLevelETag = new EntityTag(neighborhood.getNeighborhoodId().toString());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(neighborhood.getNeighborhoodId())).build();

        return Response.created(uri)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }
}
