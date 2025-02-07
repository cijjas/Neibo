package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.EventDto;
import ar.edu.itba.paw.webapp.validation.constraints.DateConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractDate;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractNullableDate;

/*
 * # Summary
 *   - A Neighborhood has many Events
 *   - A User can attend multiple Events, and the Events can have multiple Attendees
 *   - An Event start time and end time are references to Shifts
 *
 * # Use cases
 *   - An Admin can create Events for its Neighborhoods
 *   - An Admin can update/delete an Event
 *   - A Neighbor/Admin can list the future Events for their Neighborhood
 *   - A Neighbor/Admin can attend an Event
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.EVENTS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class EventController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);
    private final EventService es;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public EventController(EventService es) {
        this.es = es;
    }

    @GET
    public Response listEvents(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @QueryParam(QueryParameter.FOR_DATE) @DateConstraint String date,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Extract Date
        Date extractedDate = extractNullableDate(date);

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
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.EVENTS),
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
    @Path("{" + PathParameter.EVENT_ID + "}")
    public Response findEvent(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.EVENT_ID) long eventId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Event event = es.findEvent(neighborhoodId, eventId).orElseThrow(NotFoundException::new);
        String eventHashCode = String.valueOf(event.hashCode());

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
    @Validated(CreateSequence.class)
    public Response createEvent(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @Valid @NotNull EventDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & HashCode Generation
        final Event event = es.createEvent(neighborhoodId, createForm.getName(), createForm.getDescription(), extractDate(createForm.getEventDate()), createForm.getStartTime(), createForm.getEndTime());
        String eventHashCode = String.valueOf(event.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(event.getEventId())).build();

        return Response.created(uri)
                .tag(eventHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.EVENT_ID + "}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @Validated(UpdateSequence.class)
    public Response updateEvent(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.EVENT_ID) long eventId,
            @Valid @NotNull EventDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '{}'", uriInfo.getRequestUri());

        // Modification & HashCode Generation
        final Event updatedEvent = es.updateEvent(neighborhoodId, eventId, updateForm.getName(), updateForm.getDescription(), extractNullableDate(updateForm.getEventDate()), updateForm.getStartTime(), updateForm.getEndTime());
        String eventHashCode = String.valueOf(updatedEvent.hashCode());

        return Response.ok(EventDto.fromEvent(updatedEvent, null, uriInfo))
                .tag(eventHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.EVENT_ID + "}")
    public Response deleteEvent(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.EVENT_ID) long eventId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion attempt
        if (es.deleteEvent(neighborhoodId, eventId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
