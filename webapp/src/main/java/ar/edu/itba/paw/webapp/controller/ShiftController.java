package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.webapp.dto.BookingDto;
import ar.edu.itba.paw.webapp.dto.ShiftDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShifts(
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch
    ) {
        LOGGER.info("GET request arrived at '/shifts'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<Shift> shifts = ss.getShifts();
        if (shifts.isEmpty())
            return Response.noContent().build();
        List<ShiftDto> shiftDto = shifts.stream()
                .map(s -> ShiftDto.fromShift(s, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ShiftDto>>(shiftDto) {})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findShift(
            @PathParam("id") final long id,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch
    ) {
        LOGGER.info("GET request arrived at '/shifts/{}'", id);

        // Fetch
        Shift shift = ss.findShift(id).orElseThrow(NotFoundException::new);

        // Check Caching
        EntityTag entityTag = new EntityTag(shift.getVersion().toString());
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Fresh Copy
        return Response.ok(ShiftDto.fromShift(shift, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }
}
