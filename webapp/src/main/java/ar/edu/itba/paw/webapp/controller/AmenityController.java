package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import static ar.edu.itba.paw.webapp.controller.constants.Constant.COUNT_HEADER;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstIds;

/*
 * # Summary
 *   - A Neighborhood has many Amenities
 *   - An Amenity has many Availabilities
 *
 * # Use cases
 *   - A Neighbor/Admin can list the Amenities for their Neighborhood with the corresponding Shifts
 *   - An Admin can create, delete and update an Amenity for their Neighborhood
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.AMENITIES)
@Validated
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class AmenityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityController.class);
    private final AmenityService as;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public AmenityController(AmenityService as) {
        this.as = as;
    }

    @GET
    public Response listAmenities(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        List<Amenity> amenities = as.getAmenities(neighborhoodId, page, size);
        String amenitiesHashCode = String.valueOf(amenities.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(amenitiesHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (amenities.isEmpty())
            return Response.noContent()
                    .tag(amenitiesHashCode)
                    .build();

        List<AmenityDto> amenitiesDto = amenities.stream().map(a -> AmenityDto.fromAmenity(a, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        int amenitiesCount = as.countAmenities(neighborhoodId);
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.AMENITIES),
                amenitiesCount,
                page,
                size
        );

        return Response.ok(new GenericEntity<List<AmenityDto>>(amenitiesDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(amenitiesHashCode)
                .header(COUNT_HEADER, amenitiesCount)
                .build();
    }

    @GET
    @Path("{" + PathParameter.AMENITY_ID + "}")
    public Response findAmenity(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.AMENITY_ID) long amenityId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Amenity amenity = as.findAmenity(neighborhoodId, amenityId).orElseThrow(NotFoundException::new);
        String amenityHashCode = String.valueOf(amenity.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(amenityHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(AmenityDto.fromAmenity(amenity, uriInfo))
                .cacheControl(cacheControl)
                .tag(amenityHashCode)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    @PreAuthorize("@accessControlHelper.canCreateOrUpdateAmenity(#createForm.selectedShifts)")
    public Response createAmenity(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @Valid @NotNull AmenityDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & HashCode Generation
        Amenity amenity = as.createAmenity(neighborhoodId, createForm.getName(), createForm.getDescription(), extractFirstIds(createForm.getSelectedShifts()));
        String amenityHashCode = String.valueOf(amenity.hashCode());

        // Resource URI
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(amenity.getAmenityId())).build();

        return Response.created(uri)
                .tag(amenityHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.AMENITY_ID + "}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canCreateOrUpdateAmenity(#updateForm.selectedShifts)")
    @Validated(UpdateSequence.class)
    public Response updateAmenity(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.AMENITY_ID) long amenityId,
            @Valid @NotNull AmenityDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '{}'", uriInfo.getRequestUri());

        // Modification & HashCode Generation
        final Amenity updatedAmenity = as.updateAmenity(neighborhoodId, amenityId, updateForm.getName(), updateForm.getDescription(), extractFirstIds(updateForm.getSelectedShifts()));
        String updatedAmenityHashCode = String.valueOf(updatedAmenity.hashCode());

        // Return the updated resource along with the EntityLevelETag
        return Response.ok(AmenityDto.fromAmenity(updatedAmenity, uriInfo))
                .tag(updatedAmenityHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.AMENITY_ID + "}")
    public Response deleteAmenity(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.AMENITY_ID) long amenityId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Attempt to delete the amenity
        boolean response = as.deleteAmenity(neighborhoodId, amenityId);
        if (response)
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
