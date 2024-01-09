package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.DayOfTheWeek;
import ar.edu.itba.paw.webapp.dto.DayDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("days")
@Component
public class DayController {

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findDay(@PathParam("id") final long id) {
        DayOfTheWeek day = DayOfTheWeek.fromId((int) id);

        if (day != null) {
            DayDto dayDto = DayDto.fromDay(day, uriInfo);
            return Response.ok(dayDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Day not found").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDays() {
        List<DayDto> dayDto = Arrays.stream(DayOfTheWeek.values())
                .map(d -> DayDto.fromDay(d, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<DayDto>>(dayDto){})
                .build();
    }
}
