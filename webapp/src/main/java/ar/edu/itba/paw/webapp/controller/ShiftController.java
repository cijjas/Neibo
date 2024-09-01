package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.webapp.dto.ShiftDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.MAX_AGE_SECONDS;

/*
 * # Summary
 *   - A Shift is the combination of a Time and a Day
 *   - Shifts are utilized under the hood by Events, Availability, Bookings and more
 *
 * # Embeddable?
 *   - I think we can embed it, it would heavily reduce the amount of requests at certain points, Availabilities makes it hard
 */

@Path("shifts")
@Component
public class ShiftController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftController.class);

    @Autowired
    private ShiftService ss;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShifts(
            @QueryParam("forAmenity") final String amenity,
            @QueryParam("forDate") final Date date
    ) {
        LOGGER.info("GET request arrived at '/shifts'");

        // Content
        List<Shift> shifts = ss.getShifts(amenity, date);
        System.out.println(shifts);
        String shiftsHashCode = String.valueOf(shifts.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(shiftsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (shifts.isEmpty())
            return Response.noContent()
                    .tag(shiftsHashCode)
                    .build();

        List<ShiftDto> shiftDto = shifts.stream()
                .map(s -> ShiftDto.fromShift(s, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ShiftDto>>(shiftDto) {})
                .cacheControl(cacheControl)
                .tag(shiftsHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findShift(
            @PathParam("id") final long id
    ) {
        LOGGER.info("GET request arrived at '/shifts/{}'", id);

        // Content
        Shift shift = ss.findShift(id).orElseThrow(() -> new NotFoundException("Shift not found"));
        String shiftHashCode = String.valueOf(shift.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(shiftHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ShiftDto.fromShift(shift, uriInfo))
                .cacheControl(cacheControl)
                .tag(shiftHashCode)
                .build();
    }
}
