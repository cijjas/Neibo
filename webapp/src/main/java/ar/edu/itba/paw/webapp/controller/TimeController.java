package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import ar.edu.itba.paw.webapp.dto.TimeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("times")
@Component
public class TimeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeController.class);

    @Context
    private UriInfo uriInfo;

    // Find time by id

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTimes() {
        LOGGER.info("GET request arrived at '/times'");
        List<TimeDto> timeDto = Arrays.stream(StandardTime.values())
                .map(t -> TimeDto.fromTime(t, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<TimeDto>>(timeDto){})
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findTime(@PathParam("id") final long id) {
        LOGGER.info("GET request arrived at '/times/{}'", id);
        return Response.ok(TimeDto.fromTime(StandardTime.fromId(id), uriInfo)).build();
    }
}
