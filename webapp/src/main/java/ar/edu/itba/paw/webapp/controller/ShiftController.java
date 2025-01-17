package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.ShiftDto;
import ar.edu.itba.paw.webapp.dto.queryForms.ShiftForm;
import ar.edu.itba.paw.webapp.validation.constraints.specific.DateConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.AmenityURNConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.constants.Constant.MAX_AGE_SECONDS;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalDate;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalSecondId;

/*
 * # Summary
 *   - A Shift is the combination of a Time and a Day
 *   - Shifts are utilized under the hood by Events, Availability, Bookings and more
 *
 * # Embeddable?
 *   - I think we can embed it, it would heavily reduce the amount of requests at certain points, Availabilities makes it hard
 */

@Path(Endpoint.SHIFTS)
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
    public Response getShifts(
            @Valid @BeanParam ShiftForm shiftForm
    ) {
        LOGGER.info("GET request arrived at '/shifts'");

        // Content
        List<Shift> shifts = ss.getShifts(extractOptionalSecondId(shiftForm.getAmenity()), extractOptionalDate(shiftForm.getDate()));
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

        return Response.ok(new GenericEntity<List<ShiftDto>>(shiftDto) {
                })
                .cacheControl(cacheControl)
                .tag(shiftsHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.SHIFT_ID + "}")
    public Response findShift(
            @PathParam(PathParameter.SHIFT_ID) @GenericIdConstraint long shiftId
    ) {
        LOGGER.info("GET request arrived at '/shifts/{}'", shiftId);

        // Content
        Shift shift = ss.findShift(shiftId).orElseThrow(NotFoundException::new);
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
