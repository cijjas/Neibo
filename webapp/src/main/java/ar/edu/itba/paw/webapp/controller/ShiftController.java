package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.webapp.dto.ShiftDto;
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

    @Autowired
    private ShiftService ss;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShifts(
            @QueryParam("amenityId") long amenityId,
            @QueryParam("dayId") long dayId,
            @QueryParam("date") Date date) {

        List<Shift> shifts = ss.getShiftsByCriteria(amenityId, dayId, date);

        // Convert shifts to DTOs if needed
        List<ShiftDto> shiftDto = shifts.stream()
                .map(s -> ShiftDto.fromShift(s, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ShiftDto>>(shiftDto) {})
                .build();
    }
}
