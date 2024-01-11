package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.dto.AttendanceDto;
import ar.edu.itba.paw.webapp.form.AmenityForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/events/{eventId}/attendance")
@Component
public class AttendanceController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceController.class);

    private final AttendanceService as;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @PathParam("eventId")
    private Long eventId;

    @Autowired
    public AttendanceController(final UserService us, final AttendanceService as) {
        super(us);
        this.as = as;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listAttendance(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size) {
        LOGGER.info("Listing Attendance for Event {}", eventId);
        final Set<Attendance> attendance = as.getAttendance(eventId, page, size);
        final Set<AttendanceDto> attendanceDto = attendance.stream()
                .map(a -> AttendanceDto.fromAttendance(a, uriInfo)).collect(Collectors.toSet());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/events/" + eventId + "/attendance";
        int totalAttendancePages = as.getTotalAttendancePages(neighborhoodId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalAttendancePages);

        return Response.ok(new GenericEntity<Set<AttendanceDto>>(attendanceDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findAttendance(@PathParam("id") final long id) {
        LOGGER.info("Finding Attendance with id {}", id);
        return Response.ok(AttendanceDto.fromAttendance(as.findAttendanceById(id)
                .orElseThrow(() -> new NotFoundException("Attendance Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createAttendance() {
        LOGGER.info("Creating Attendance for Event {}", eventId);
        final Attendance attendance = as.createAttendee(getLoggedUser().getUserId(), eventId);
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(attendance.getId())).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteByUser() {
        LOGGER.info("Deleting Attendance for User {}", getLoggedUser().getUserId());
        as.deleteAttendee(getLoggedUser().getUserId(), eventId);
        return Response.noContent().build();
    }
}

