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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDays() {
        LOGGER.info("GET request arrived at days");
        List<DayDto> dayDto = Arrays.stream(DayOfTheWeek.values())
                .map(d -> DayDto.fromDay(d, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<DayDto>>(dayDto){})
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findDay(@PathParam("id") final long id) {
        LOGGER.info("GET request arrived at days/{}", id);
        return Response.ok(DayDto.fromDay(DayOfTheWeek.fromId(id), uriInfo)).build();
    }
}
