package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.webapp.dto.BookingDto;
import ar.edu.itba.paw.webapp.dto.ShiftDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShifts() {
        LOGGER.info("GET request arrived at shifts");
        List<Shift> shifts = ss.getShifts();

        // Convert shifts to DTOs if needed
        List<ShiftDto> shiftDto = shifts.stream()
                .map(s -> ShiftDto.fromShift(s, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ShiftDto>>(shiftDto) {})
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findShift(@PathParam("id") final long id) {
        LOGGER.info("GET request arrived at shifts/{}", id);
        return Response.ok(ShiftDto.fromShift(ss.findShift(id)
                .orElseThrow(() -> new NotFoundException("Shift Not Found")), uriInfo)).build();
    }
}
