package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.dto.AttendanceDto;
import ar.edu.itba.paw.webapp.dto.AttendanceCountDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractSecondId;

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
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class AttendanceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final AttendanceService as;
    private final EventService es;

    @Autowired
    public AttendanceController(AttendanceService as, EventService es) {
        this.as = as;
        this.es = es;
    }

    @GET
    public Response listAttendance(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("eventId") @GenericIdConstraint final Long eventId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events/{}/attendance'", neighborhoodId, eventId);

        // Path Verification
        es.findEvent(neighborhoodId, eventId).orElseThrow(NotFoundException::new);

        // Content
        final List<Attendance> attendance = as.getAttendance(neighborhoodId, eventId, size, page);
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

        return Response.ok(new GenericEntity<List<AttendanceDto>>(attendanceDto) {
                })
                .cacheControl(cacheControl)
                .tag(attendanceHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/count")
    public Response countAttendance(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("eventId") @GenericIdConstraint final Long eventId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events/{}/attendance/count'", neighborhoodId, eventId);

        // Content
        int count = as.countAttendance(neighborhoodId, eventId);
        String countHashCode = String.valueOf(count);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(countHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        AttendanceCountDto dto = AttendanceCountDto.fromAttendanceCount(count, eventId, neighborhoodId,  uriInfo);

        return Response.ok(new GenericEntity<AttendanceCountDto>(dto) {
                })
                .cacheControl(cacheControl)
                .tag(countHashCode)
                .build();
    }

    @GET
    @Path("/{userId}")
    public Response findAttendance(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("eventId") @GenericIdConstraint final Long eventId,
            @PathParam("userId") @GenericIdConstraint final Long userId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events/{}/attendance/{}'", neighborhoodId, eventId, userId);

        // Content
        Attendance attendance = as.findAttendance(neighborhoodId, eventId, userId).orElseThrow(NotFoundException::new);
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
    @Validated(CreateValidationSequence.class)
    public Response createAttendance(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("eventId") @GenericIdConstraint final Long eventId,
            @Valid final AttendanceDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/events/{}/attendance'", neighborhoodId, eventId);

        // Path Verification
        es.findEvent(neighborhoodId, eventId).orElseThrow(NotFoundException::new);

        // Creation & HashCode Generation
        final Attendance attendance = as.createAttendance(extractSecondId(form.getUser()), eventId);
        String attendanceHashCode = String.valueOf(attendance.hashCode());

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(attendance.getId().getUserId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(attendanceHashCode)
                .build();
    }

    @DELETE
    @Path("/{userId}")
    @PreAuthorize("@pathAccessControlHelper.canDeleteAttendance(#userId)")
    public Response deleteAttendance(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("eventId") @GenericIdConstraint final Long eventId,
            @PathParam("userId") @GenericIdConstraint final long userId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/events/{}/attendance'", neighborhoodId, eventId);

        // Path Verification
        es.findEvent(neighborhoodId, eventId).orElseThrow(NotFoundException::new);

        // Deletion Attempt
        if (as.deleteAttendance(userId, eventId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
