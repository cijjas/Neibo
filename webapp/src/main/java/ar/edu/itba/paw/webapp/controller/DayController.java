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

@Path("days")
@Component
public class DayController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DayController.class);

    @Context
    private UriInfo uriInfo;

    private final EntityTag storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDays(
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context Request request
    ) {
        LOGGER.info("GET request arrived at '/days'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        cacheControl.setMaxAge(3600);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<DayDto> dayDto = Arrays.stream(DayOfTheWeek.values())
                .map(d -> DayDto.fromDay(d, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<DayDto>>(dayDto){})
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findDay(
            @PathParam("id") final long id,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context Request request
    ) {
        LOGGER.info("GET request arrived at '/days/{}'", id);

        DayDto dayDto = DayDto.fromDay(DayOfTheWeek.fromId(id), uriInfo);

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null) {
            return builder.cacheControl(cacheControl).build();
        }

        return Response.ok(dayDto)
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }
}
