package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.ShiftStatus;
import ar.edu.itba.paw.webapp.dto.ShiftStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;

/*
 * # Summary
 *   - Maybe renaming to Availability Status would be more accurate
 *   - Availability Criteria, that way you can get the FREE and the TAKEN Shifts for a certain Amenity on a certain date, for Booking
 *
 * # Use cases
 *   - A User/Admin filter the Availabilities through a certain Shift Status usually to make a Booking
 */

@Path("shift-statuses")
@Component
public class ShiftStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftStatusController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listShiftStatuses() {
        LOGGER.info("GET request arrived at '/shift-statuses'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<ShiftStatusDto> shiftStatusDto = Arrays.stream(ShiftStatus.values())
                .map(tt -> ShiftStatusDto.fromShiftStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ShiftStatusDto>>(shiftStatusDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findShiftStatus(
            @PathParam("id") final int id,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/shift-statuses/{}'", id);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(Long.toString(id));
        Response response = checkETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Content
        ShiftStatusDto shiftStatusDto = ShiftStatusDto.fromShiftStatus(ShiftStatus.fromId(id), uriInfo);

        return Response.ok(shiftStatusDto)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .header(HttpHeaders.CACHE_CONTROL, MAX_AGE_HEADER)
                .build();
    }
}