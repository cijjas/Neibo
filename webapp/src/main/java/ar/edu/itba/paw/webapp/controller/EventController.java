package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.webapp.dto.EventDto;
import ar.edu.itba.paw.webapp.security.UserAuth;
import ar.edu.itba.paw.webapp.validation.constraints.specific.DateConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractDate;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalDate;


/*
 * # Summary
 *   - A Neighborhood has many Events
 *   - A User can attend multiple Events, and the Events can have multiple Attendees
 *   - An event has a relationship with a Shifts
 *
 * # Use cases
 *   - An Admin can create Events for its Neighborhoods
 *   - An Admin can update/delete an Event
 *   - A User/Admin can list the future Events for their Neighborhood
 *   - A User/Admin can attend an Event (maybe an Admin should not be able to, though it currently helps int testing)
 */

@Path("neighborhoods/{neighborhoodId}/events")
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class EventController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final EventService es;
    private final AttendanceService as;

    @Autowired
    public EventController(EventService es, AttendanceService as) {
        this.es = es;
        this.as = as;
    }

    @GET
    public Response listEvents(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam("forDate") @DateConstraint String date,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events'", neighborhoodId);

        // Extract Date
        Date extractedDate = extractOptionalDate(date);

        // Content
        final List<Event> events = es.getEvents(neighborhoodId, extractedDate, page, size);
        String eventsHashCode = String.valueOf(events.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(eventsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (events.isEmpty())
            return Response.noContent()
                    .tag(eventsHashCode)
                    .build();

        final List<EventDto> eventsDto = events.stream()
                .map(e -> EventDto.fromEvent(e, null, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/events",
                es.calculateEventPages(neighborhoodId, extractedDate, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<EventDto>>(eventsDto) {
                })
                .cacheControl(cacheControl)
                .tag(eventsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/{eventId}")
    public Response findEvent(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @PathParam("eventId") @GenericIdConstraint long eventId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events/{}'", neighborhoodId, eventId);

        // Content
        Event event = es.findEvent(neighborhoodId, eventId).orElseThrow(NotFoundException::new);
        // Add to JWT the userId information
        String eventHashCode = String.valueOf(event.hashCode()); // this hash code should also change

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(eventHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(EventDto.fromEvent(event, null, uriInfo))
                .cacheControl(cacheControl)
                .tag(eventHashCode)
                .build();
    }

    @POST
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Validated(CreateValidationSequence.class)
    public Response createEvent(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @Valid EventDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/events'", neighborhoodId);

        // Creation & HashCode Generation
        final Event event = es.createEvent(neighborhoodId, createForm.getName(), createForm.getDescription(), extractDate(createForm.getEventDate()), createForm.getStartTime(), createForm.getEndTime());
        String eventHashCode = String.valueOf(event.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(event.getEventId())).build();

        return Response.created(uri)
                .tag(eventHashCode)
                .build();
    }

    @PATCH
    @Path("/{eventId}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Validated(UpdateValidationSequence.class)
    public Response updateEvent(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @PathParam("eventId") @GenericIdConstraint long eventId,
            @Valid EventDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/events/{}'", neighborhoodId, eventId);

        // Modification & HashCode Generation
        final Event updatedEvent = es.updateEvent(neighborhoodId, eventId, updateForm.getName(), updateForm.getDescription(), extractDate(updateForm.getEventDate()), updateForm.getStartTime(), updateForm.getEndTime());
        String eventHashCode = String.valueOf(updatedEvent.hashCode());

        return Response.ok(EventDto.fromEvent(updatedEvent, null, uriInfo))
                .tag(eventHashCode)
                .build();
    }

    @DELETE
    @Path("/{eventId}")
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    public Response deleteEvent(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @PathParam("eventId") @GenericIdConstraint long eventId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/events/{}'", neighborhoodId, eventId);

        // Deletion attempt
        if (es.deleteEvent(neighborhoodId, eventId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
