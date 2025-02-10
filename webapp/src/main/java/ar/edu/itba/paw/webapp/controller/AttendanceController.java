package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.AttendanceDto;
import ar.edu.itba.paw.webapp.dto.queryForms.AttendanceParams;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.DeleteSequence;
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
import static ar.edu.itba.paw.webapp.controller.constants.Constant.COUNT_HEADER;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.*;

/*
 * # Summary
 *   - Junction Table between Events and Users
 *
 * # Use cases
 *   - A Neighbor/Admin can list the Events he plans to attend
 *   - A Neighbor/Admin can list the Users that will attend a certain Event
 *   - A Neighbor/Admin can notify his Attendance to a certain Event
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.ATTENDANCE)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class AttendanceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceController.class);

    private final AttendanceService as;
    private final NeighborhoodController neighborhoodController;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @Autowired
    public AttendanceController(AttendanceService as, NeighborhoodController neighborhoodController) {
        this.as = as;
        this.neighborhoodController = neighborhoodController;
    }

    @GET
    @PreAuthorize("@accessControlHelper.canListOrCountAttendance(#attendanceParams.event, #attendanceParams.user)")
    public Response listAttendance(
            @Valid @BeanParam AttendanceParams attendanceParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long eventId = extractNullableSecondId(attendanceParams.getEvent());
        Long userId = extractNullableFirstId(attendanceParams.getUser());

        // Content
        final List<Attendance> attendance = as.getAttendance(attendanceParams.getNeighborhoodId(), eventId, userId, attendanceParams.getPage(), attendanceParams.getSize());
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
        int attendanceCount = as.countAttendance(attendanceParams.getNeighborhoodId(), eventId, userId);
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(attendanceParams.getNeighborhoodId())).path(Endpoint.EVENTS).path(String.valueOf(eventId)).path(Endpoint.ATTENDANCE),
                attendanceCount,
                attendanceParams.getPage(),
                attendanceParams.getSize()
        );

        return Response.ok(new GenericEntity<List<AttendanceDto>>(attendanceDto) {
                })
                .cacheControl(cacheControl)
                .tag(attendanceHashCode)
                .links(links)
                .header(COUNT_HEADER, attendanceCount)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    @PreAuthorize("@accessControlHelper.canCreateOrDeleteAttendance(#createForm.event, #createForm.user)")
    public Response createAttendance(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @Valid @NotNull AttendanceDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & HashCode Generation
        final Attendance attendance = as.createAttendance(extractSecondId(createForm.getEvent()), extractFirstId(createForm.getUser()));
        String attendanceHashCode = String.valueOf(attendance.hashCode());

        AttendanceDto attendanceDto = AttendanceDto.fromAttendance(attendance, uriInfo);

        return Response.created(attendanceDto.get_links().getSelf())
                .tag(attendanceHashCode)
                .build();
    }

    @DELETE
    @PreAuthorize("@accessControlHelper.canCreateOrDeleteAttendance(#attendanceParams.event, #attendanceParams.user)")
    @Validated(DeleteSequence.class)
    public Response deleteAttendance(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @Valid @BeanParam AttendanceParams attendanceParams
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (as.deleteAttendance(extractSecondId(attendanceParams.getEvent()), extractFirstId(attendanceParams.getUser())))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
