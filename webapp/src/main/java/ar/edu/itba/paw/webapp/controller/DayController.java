package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.DayOfTheWeek;
import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.webapp.dto.DayDto;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
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
 *   - This entity is referenced across multiple sections of the applications, like Bookings, Events and Availabilities
 *
 * # Use cases
 *   - Used as proxy in Bookings, Events, Availabilities
 *
 * # Embeddable? Hopefully yes
 *   - Best case scenario this is nested in every entity that uses it, I think it is possible to achieve that
 *   - It is also never used as a filter, if I recall correctly
 */

@Path("days")
@Component
public class DayController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DayController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDays() {
        LOGGER.info("GET request arrived at '/days'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder
                    .cacheControl(cacheControl)
                    .tag(entityLevelETag)
                    .build();

        // Content
        List<DayDto> dayDto = Arrays.stream(DayOfTheWeek.values())
                .map(d -> DayDto.fromDay(d, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<DayDto>>(dayDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findDay(
            @PathParam("id") final long dayId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/days/{}'", dayId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(Long.toString(dayId));
        Response response = checkETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Content
        DayDto dayDto = DayDto.fromDay(DayOfTheWeek.fromId(dayId), uriInfo);

        return Response.ok(dayDto)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .header(HttpHeaders.CACHE_CONTROL, MAX_AGE_HEADER)
                .build();
    }
}
