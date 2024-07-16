package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.interfaces.services.AvailabilityService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.AvailabilityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.CUSTOM_ROW_LEVEL_ETAG_NAME;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.MAX_AGE_HEADER;

/*
 * # Summary
 *   - Junction Table between Amenities and Shifts
 *
 * # Use cases
 *   - An Admin can update the Availability of an Amenity
 *   - A User/Admin can list the Availabilities for a certain Amenity
 *
 * # Embeddable? I don't know
 *   - An amenity can have an attribute called availability that is an array of shifts
 *   - The array has a fixed maximum size
 *   - It is only accessed from the Amenity POV BUT the Events also use them, does that create some kind of conflict?
 *   - Does being a pair create some kind of conflict?
 *   - A Booking has to reference a unique Availability which pushes into a differentiated nested weak entity (how it currently is), or maybe the booking can also nest the entity
 *   {
        "availability": "http://localhost:8080/neighborhoods/1/amenities/1/availability",
        "description": "Super Amenity",
        "name": "Amenity",
        "availability": [{day: tuesday, time:15:00}, {day: tuesday, time:18:00}, {day: tuesday, time:17:00}],
        "neighborhood": "http://localhost:8080/neighborhoods/1",
        "self": "http://localhost:8080/neighborhoods/1/amenities/1"
    }
 */

@Path("neighborhoods/{neighborhoodId}/amenities/{amenityId}/availability")
@Component
public class AvailabilityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityController.class);

    @Autowired
    private AvailabilityService as;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @PathParam("amenityId")
    private Long amenityId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listAvailability(
            @QueryParam("withStatus") String status,
            @QueryParam("forDate") String date
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/amenities/{}/availability'", neighborhoodId, amenityId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Availability> availabilities = as.getAvailability(amenityId, status, date, neighborhoodId);
        if (availabilities.isEmpty())
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        final List<AvailabilityDto> availabilityDto = availabilities.stream()
                .map(a -> AvailabilityDto.fromAvailability(a, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<AvailabilityDto>>(availabilityDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findAvailability(
            @PathParam("id") final long availabilityId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/amenities/{}/availability/{}'", neighborhoodId, amenityId, availabilityId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(String.valueOf(availabilityId));
        Response response = checkETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Content
        AvailabilityDto availabilityDto = AvailabilityDto.fromAvailability(as.findAvailability(amenityId, availabilityId, neighborhoodId).orElseThrow(NotFoundException::new), uriInfo);

        return Response.ok(availabilityDto)
                .tag(entityLevelETag)
                .header(HttpHeaders.CACHE_CONTROL, MAX_AGE_HEADER)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }
}
