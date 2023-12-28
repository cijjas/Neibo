package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import ar.edu.itba.paw.webapp.dto.TimeDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("times")
@Component
public class TimeController {

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTimes() {
        List<TimeDto> timeDto = Arrays.stream(StandardTime.values())
                .map(t -> TimeDto.fromTime(t, uriInfo))
                .collect(Collectors.toList());

//        return Response.ok(timeDto).build();

        return Response.ok(new GenericEntity<List<TimeDto>>(timeDto){})
                .build();
    }

}
