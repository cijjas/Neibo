package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.dto.AttendanceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.*;

/*
 * # Summary
 *   - Junction Table between Events and Users
 *
 * # Use cases
 *   - A User/Admin can list the Events he plans to attend
 *   - A User/Admin can list the Users that will attend a certain Event
 *   - A User/Admin can confirm his attendance to a certain event
 */

@Path("neighborhoods/{neighborhoodId}/events/{eventId}/attendance")
@Component
public class AttendanceController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    private AttendanceService as;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @PathParam("eventId")
    private Long eventId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listAttendance(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events/{}/attendance'", neighborhoodId, eventId);

        // Content
        final List<Attendance> attendance = as.getAttendance(eventId, page, size, neighborhoodId);
        String attendanceHashCode = String.valueOf(attendance.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(attendanceHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (attendance.isEmpty())
            return Response.noContent()
                    .tag(attendanceHashCode)
                    .build();

        final List<AttendanceDto> attendanceDto = attendance.stream()
                .map(a -> AttendanceDto.fromAttendance(a, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/events/" + eventId + "/attendance",
                as.calculateAttendancePages(neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<AttendanceDto>>(attendanceDto){})
                .cacheControl(cacheControl)
                .tag(attendanceHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/{userId}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findAttendance(
            @PathParam("userId") final long userId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events/{}/attendance/{}'", neighborhoodId, eventId, userId);

        // Content
        Attendance attendance = as.findAttendance(userId, eventId, neighborhoodId).orElseThrow(() -> new NotFoundException("Attendance Not Found"));
        String attendanceHashCode = String.valueOf(attendance.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(attendanceHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(AttendanceDto.fromAttendance(attendance, uriInfo))
                .cacheControl(cacheControl)
                .tag(attendanceHashCode)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createAttendance() {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/events/{}/attendance'", neighborhoodId, eventId);

        // Creation & HashCode Generation
        final Attendance attendance = as.createAttendance(getRequestingUserId(), eventId);
        String attendanceHashCode = String.valueOf(attendance.hashCode());

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(attendance.getId().getUserId())).build();

        return Response.created(uri)
                .tag(attendanceHashCode)
                .build();
    }

    @DELETE
    @Path("/{userId}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteByUser(
            @PathParam("userId") final long userId,
            @HeaderParam(HttpHeaders.IF_MATCH) EntityTag ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/events/{}/attendance/{}'", neighborhoodId, eventId, userId);

        // Deletion Attempt
        if(as.deleteAttendance(userId, eventId)) {
            return Response.noContent()
                    .build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
