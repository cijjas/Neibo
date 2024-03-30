package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.webapp.dto.AttendanceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @Autowired
    public AttendanceController(final UserService us) {
        super(us);
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listAttendance(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events/{}/attendance'", neighborhoodId, eventId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final Set<Attendance> attendance = as.getAttendance(eventId, page, size, neighborhoodId);
        if (attendance.isEmpty())
            return Response.noContent().build();
        final Set<AttendanceDto> attendanceDto = attendance.stream()
                .map(a -> AttendanceDto.fromAttendance(a, uriInfo)).collect(Collectors.toSet());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/events/" + eventId + "/attendance",
                as.calculateAttendancePages(neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<Set<AttendanceDto>>(attendanceDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .links(links)
                .build();
    }

    @GET
    @Path("/{userId}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findAttendance(
            @PathParam("userId") final long userId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events/{}/attendance/{}'", neighborhoodId, eventId, userId);

        // Content
        Attendance attendance = as.findAttendance(userId, eventId, neighborhoodId).orElseThrow(NotFoundException::new);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        EntityTag entityTag = new EntityTag(attendance.getVersion().toString());
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(AttendanceDto.fromAttendance(attendance, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createAttendance() {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/events/{}/attendance'", neighborhoodId, eventId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Creation & ETag Generation
        final Attendance attendance = as.createAttendance(getLoggedUser().getUserId(), eventId);
        entityLevelETag = ETagUtility.generateETag();

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(attendance.getId())).build();

        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @DELETE
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteByUser(
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/events/{}/attendance'", neighborhoodId, eventId);

        // Cache Control
        if (ifMatch != null) {
            Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, entityLevelETag)
                        .build();
        }

        // Deletion & ETag Generation Attempt
        if(as.deleteAttendance(getLoggedUser().getUserId(), eventId)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}

