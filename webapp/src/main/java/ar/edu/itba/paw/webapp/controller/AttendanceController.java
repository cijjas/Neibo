package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
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

@Path(Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.ATTENDANCE)
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
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam(QueryParameter.FOR_EVENT) @EventURNConstraint String event,
            @QueryParam(QueryParameter.FOR_USER) @UserURNConstraint String user,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
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
                uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.EVENTS).path(String.valueOf(eventId)).path(Endpoint.ATTENDANCE),
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
    @Path(Endpoint.COUNT)
    public Response countAttendance(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam(QueryParameter.FOR_EVENT) @EventURNConstraint String event,
            @QueryParam(QueryParameter.FOR_USER) @UserURNConstraint String user
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
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @Valid @NotNull AttendanceDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/attendance'", neighborhoodId);

        // Creation & HashCode Generation
        final Attendance attendance = as.createAttendance(extractSecondId(createForm.getEvent()), extractSecondId(createForm.getUser()));
        String attendanceHashCode = String.valueOf(attendance.hashCode());

        AttendanceDto attendanceDto = AttendanceDto.fromAttendance(attendance, uriInfo);

        return Response.created(attendanceDto.get_links().getSelf())
                .tag(attendanceHashCode)
                .build();
    }

    @DELETE
    @PreAuthorize("@pathAccessControlHelper.canDeleteAttendance(#user)")
    public Response deleteAttendance(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam(QueryParameter.FOR_EVENT) @EventURNConstraint String event,
            @QueryParam(QueryParameter.FOR_USER) @UserURNConstraint String user
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
