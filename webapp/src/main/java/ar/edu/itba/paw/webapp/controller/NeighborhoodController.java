package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.controller.constants.*;
import ar.edu.itba.paw.webapp.dto.NeighborhoodDto;
import ar.edu.itba.paw.webapp.dto.queryForms.NeighborhoodParams;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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
 *   - Main entity in the Application
 *   - Many Neighborhoods live under the same Database, so most entities have to have a reference to the Neighborhood they belong to
 *   - Has relationships with Users, Posts, Channels, Products and many more
 *
 * # Use cases
 *   - Anyone can register as UNVERIFIED to any Non Base Neighborhood
 *   - Registered Users can migrate Neighborhood but becoming UNVERIFIED in the process
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class NeighborhoodController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodController.class);
    private final NeighborhoodService ns;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public NeighborhoodController(NeighborhoodService ns) {
        this.ns = ns;
    }

    @GET
    @PreAuthorize("@accessControlHelper.canListNeighborhoods(#neighborhoodParams.withWorker, #neighborhoodParams.withoutWorker)")
    public Response listNeighborhoods(
            @Valid @BeanParam NeighborhoodParams neighborhoodParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long withWorkerId = extractOptionalFirstId(neighborhoodParams.getWithWorker());
        Long withoutWorkerId = extractOptionalFirstId(neighborhoodParams.getWithoutWorker());

        // Content
        final List<Neighborhood> neighborhoods = ns.getNeighborhoods(neighborhoodParams.getBase(), withWorkerId, withoutWorkerId, neighborhoodParams.getPage(), neighborhoodParams.getSize());
        String neighborhoodsHashCode = String.valueOf(neighborhoods.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(neighborhoodsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (neighborhoods.isEmpty())
            return Response.noContent()
                    .tag(neighborhoodsHashCode)
                    .build();

        final List<NeighborhoodDto> neighborhoodsDto = neighborhoods.stream()
                .map(n -> NeighborhoodDto.fromNeighborhood(n, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS),
                ns.calculateNeighborhoodPages(neighborhoodParams.getBase(), withWorkerId, withoutWorkerId, neighborhoodParams.getSize()),
                neighborhoodParams.getPage(),
                neighborhoodParams.getSize()
        );

        return Response.ok(new GenericEntity<List<NeighborhoodDto>>(neighborhoodsDto) {
                })
                .cacheControl(cacheControl)
                .tag(neighborhoodsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("{" + PathParameter.NEIGHBORHOOD_ID + "}")
    public Response findNeighborhood(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Neighborhood neighborhood = ns.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);
        String neighborhoodHashCode = String.valueOf(neighborhood.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(neighborhoodHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(NeighborhoodDto.fromNeighborhood(neighborhood, uriInfo))
                .cacheControl(cacheControl)
                .tag(neighborhoodHashCode)
                .build();
    }

    @POST
    @Secured(UserRole.SUPER_ADMINISTRATOR)
    @Validated(CreateSequence.class)
    public Response createNeighborhood(
            @Valid @NotNull NeighborhoodDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & HashCode Generation
        final Neighborhood neighborhood = ns.createNeighborhood(createForm.getName());
        String neighborhoodHashCode = String.valueOf(neighborhood.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(neighborhood.getNeighborhoodId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(neighborhoodHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.NEIGHBORHOOD_ID + "}")
    @Secured(UserRole.SUPER_ADMINISTRATOR)
    public Response deleteNeighborhood(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (ns.deleteNeighborhood(neighborhoodId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
