package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.dto.AttendanceCountDto;
import ar.edu.itba.paw.webapp.dto.AttendanceDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.EventURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalSecondId;
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

@Path("neighborhoods/{neighborhoodId}/attendance")
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class AttendanceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceController.class);

    private final AttendanceService as;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @Autowired
    public AttendanceController(AttendanceService as) {
        this.as = as;
    }

    @GET
    public Response listAttendance(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam("forEvent") @EventURNConstraint String event,
            @QueryParam("forUser") @UserURNConstraint String user,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/attendance'", neighborhoodId);

        // ID Extraction
        Long eventId = extractOptionalSecondId(event);
        Long userId = extractOptionalSecondId(user);

        // Content
        final List<Attendance> attendance = as.getAttendance(neighborhoodId, eventId, userId, size, page);
        String attendanceHashCode;

        // This is required to keep a consistent hash code across creates and this endpoint used as a find
        if (attendance.size() == 1) {
            Attendance singleAttendance = attendance.get(0);
            attendanceHashCode = String.valueOf(singleAttendance.hashCode());
        } else {
            attendanceHashCode = String.valueOf(attendance.hashCode());
        }

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
                as.calculateAttendancePages(neighborhoodId, userId, eventId, size),
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
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam("forEvent") @EventURNConstraint String event,
            @QueryParam("forUser") @UserURNConstraint String user
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/attendance/count'", neighborhoodId);

        // ID Extraction
        Long eventId = extractOptionalSecondId(event);
        Long userId = extractOptionalSecondId(user);

        // Content
        int count = as.countAttendance(neighborhoodId, eventId, userId);
        String countHashCode = String.valueOf(count);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(countHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        AttendanceCountDto dto = AttendanceCountDto.fromAttendanceCount(count, neighborhoodId, event, user, uriInfo);

        return Response.ok(new GenericEntity<AttendanceCountDto>(dto) {
                })
                .cacheControl(cacheControl)
                .tag(countHashCode)
                .build();
    }

    @POST
    @Validated(CreateValidationSequence.class)
    public Response createAttendance(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @Valid @NotNull AttendanceDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/attendance'", neighborhoodId);

        // Creation & HashCode Generation
        final Attendance attendance = as.createAttendance(extractSecondId(createForm.getEvent()), extractSecondId(createForm.getUser()));
        String attendanceHashCode = String.valueOf(attendance.hashCode());

        final URI uri = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("attendance")
                .queryParam("forEvent",
                        uriInfo.getBaseUriBuilder()
                                .path("neighborhoods")
                                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                                .path("events")
                                .path(String.valueOf(attendance.getEvent().getEventId()))
                                .build())
                .queryParam("forUser",
                        uriInfo.getBaseUriBuilder()
                                .path("neighborhoods")
                                .path(String.valueOf(attendance.getEvent().getNeighborhood().getNeighborhoodId()))
                                .path("users")
                                .path(String.valueOf(attendance.getUser().getUserId()))
                                .build())
                .build();

        return Response.created(uri)
                .tag(attendanceHashCode)
                .build();
    }

    @DELETE
    @PreAuthorize("@pathAccessControlHelper.canDeleteAttendance(#user)")
    public Response deleteAttendance(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam("forEvent") @EventURNConstraint String event,
            @QueryParam("forUser") @UserURNConstraint String user
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/attendance'", neighborhoodId);

        // Deletion Attempt
        if (as.deleteAttendance(extractOptionalSecondId(event), extractSecondId(user)))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
