package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.Entities.Time;
import ar.edu.itba.paw.webapp.dto.DepartmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*@Path("times")
@Component*/
public class TimeController {

    @Context
    private UriInfo uriInfo;

    /*
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTimes() {
        List<TimeDto> timeDto = Arrays.stream(StandardTime.values())
                .map(d -> TimeDto.fromTime(d, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(timeDto).build();
    }
    */
}
