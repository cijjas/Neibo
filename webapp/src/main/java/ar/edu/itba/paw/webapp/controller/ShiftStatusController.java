package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.ShiftStatus;
import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.webapp.dto.ShiftStatusDto;
import ar.edu.itba.paw.webapp.dto.TimeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("shift-statuses")
@Component
public class ShiftStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftStatusController.class);

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listShiftStatuses() {
        LOGGER.info("GET request arrived at '/shift-statuses'");
        List<ShiftStatusDto> shiftStatusDto = Arrays.stream(ShiftStatus.values())
                .map(tt -> ShiftStatusDto.fromShiftStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ShiftStatusDto>>(shiftStatusDto){}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findShiftStatus(@PathParam("id") final int id) {
        LOGGER.info("GET request arrived at '/shift-statuses/{}'", id);
        return Response.ok(ShiftStatusDto.fromShiftStatus(ShiftStatus.fromId(id), uriInfo)).build();
    }
}