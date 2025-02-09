package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.ShiftDto;
import ar.edu.itba.paw.webapp.dto.queryForms.ShiftParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.constants.Constant.IMMUTABLE;
import static ar.edu.itba.paw.webapp.controller.constants.Constant.MAX_AGE_SECONDS;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractNullableDate;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractNullableSecondId;

/*
 * # Summary
 *   - A Shift is the combination of a Time and a Day
 *   - Shifts are utilized under the hood by Events, Availability, Bookings and more
 *
 * # Use cases
 *   - Anyone can list the Shifts
 *   - A Neighbor/Admin can get the Shifts when an Amenity is available
 *   - A Neighbor/Admin can get the Shifts when an Amenity is available on a specific Date
 */

@Path(Endpoint.API + "/" + Endpoint.SHIFTS)
@Component
@Produces(MediaType.APPLICATION_JSON)
public class ShiftController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftController.class);
    private final ShiftService ss;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public ShiftController(ShiftService ss) {
        this.ss = ss;
    }

    @GET
    @PreAuthorize("@accessControlHelper.canListShifts(#shiftParams.amenity)")
    public Response getShifts(
            @Valid @BeanParam ShiftParams shiftParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        List<Shift> shifts = ss.getShifts(extractNullableSecondId(shiftParams.getAmenity()), extractNullableDate(shiftParams.getDate()));
        String shiftsHashCode = String.valueOf(shifts.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
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

        return Response.ok(new GenericEntity<List<ShiftDto>>(shiftDto) {
                })
                .cacheControl(cacheControl)
                .tag(shiftsHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.SHIFT_ID + "}")
    public Response findShift(
            @PathParam(PathParameter.SHIFT_ID) long shiftId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Shift shift = ss.findShift(shiftId).orElseThrow(NotFoundException::new);
        String shiftHashCode = String.valueOf(shift.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(shiftHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ShiftDto.fromShift(shift, uriInfo))
                .cacheControl(cacheControl)
                .tag(shiftHashCode)
                .build();
    }
}
