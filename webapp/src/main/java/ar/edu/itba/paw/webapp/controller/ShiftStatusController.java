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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listShiftStatuses() {
        LOGGER.info("GET request arrived at '/shift-statuses'");

        // Content
        ShiftStatus[] shiftStatuses = ShiftStatus.values();
        String shiftStatusesHashCode = String.valueOf(Arrays.hashCode(shiftStatuses));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(shiftStatusesHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<ShiftStatusDto> shiftStatusDto = Arrays.stream(shiftStatuses)
                .map(tt -> ShiftStatusDto.fromShiftStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ShiftStatusDto>>(shiftStatusDto){})
                .cacheControl(cacheControl)
                .tag(shiftStatusesHashCode)
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

        // Content
        ShiftStatus shiftStatus = ShiftStatus.fromId(id);
        String shiftStatusHashCode = String.valueOf(shiftStatus.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(shiftStatusHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ShiftStatusDto.fromShiftStatus(shiftStatus, uriInfo))
                .cacheControl(cacheControl)
                .tag(shiftStatusHashCode)
                .build();
    }
}