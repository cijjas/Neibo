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
            return Response.noContent().build();
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
