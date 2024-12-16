package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstIds;

/*
 * # Summary
 *   - A Neighborhood has many Amenities
 *   - An Amenity has many Availabilities
 *
 * # Use cases
 *   - A User/Admin can list the Amenities for their Neighborhood with the corresponding Shifts
 *   - An Admin can create, delete and update an Amenity for their Neighborhood
 */

@Path("neighborhoods/{neighborhoodId}/amenities")
@Validated
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class AmenityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final AmenityService as;

    @Autowired
    public AmenityController(AmenityService as) {
        this.as = as;
    }

    @GET
    public Response listAmenities(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/amenities'", neighborhoodId);

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
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/amenities",
                as.calculateAmenityPages(neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<AmenityDto>>(amenitiesDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(amenitiesHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response findAmenity(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("id") @GenericIdConstraint final Long id
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        // Content
        Amenity amenity = as.findAmenity(neighborhoodId, id).orElseThrow(NotFoundException::new);
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
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Validated(CreateValidationSequence.class)
    public Response createAmenity(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @Valid AmenityDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/amenities'", neighborhoodId);

        // Creation & HashCode Generation
        Amenity amenity = as.createAmenity(neighborhoodId, form.getName(), form.getDescription(), extractFirstIds(form.getSelectedShifts()));
        String amenityHashCode = String.valueOf(amenity.hashCode());

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(amenity.getAmenityId())).build();

        return Response.created(uri)
                .tag(amenityHashCode)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Validated(UpdateValidationSequence.class)
    public Response updateAmenityPartially(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("id") @GenericIdConstraint final Long id,
            @Valid AmenityDto form
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        // Modification & HashCode Generation
        final Amenity updatedAmenity = as.updateAmenityPartially(id, form.getName(), form.getDescription(), extractFirstIds(form.getSelectedShifts()));
        String updatedAmenityHashCode = String.valueOf(updatedAmenity.hashCode());

        // Return the updated resource along with the EntityLevelETag
        return Response.ok(AmenityDto.fromAmenity(updatedAmenity, uriInfo))
                .tag(updatedAmenityHashCode)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    public Response deleteAmenity(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("id") @GenericIdConstraint final Long amenityId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, amenityId);

        // Attempt to delete the amenity
        boolean response = as.deleteAmenity(neighborhoodId, amenityId);
        if (response)
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
