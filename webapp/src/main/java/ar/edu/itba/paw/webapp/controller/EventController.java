package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.webapp.dto.EventDto;
import ar.edu.itba.paw.webapp.form.EventForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@Path("neighborhoods/{neighborhoodId}/events")
@Component
public class EventController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService es;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listEventsByDate(
            @QueryParam("date") final String date
            ) {
        LOGGER.info("GET request arrived at neighborhoods/{}/events", neighborhoodId);
        final List<Event> events = es.getEvents(date, neighborhoodId);
        final List<EventDto> eventsDto = events.stream()
                .map(e -> EventDto.fromEvent(e, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<EventDto>>(eventsDto){})
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findEvent(@PathParam("id") final long id) {
        LOGGER.info("GET request arrived at neighborhoods/{}/events/{}", neighborhoodId, id);
        return Response.ok(EventDto.fromEvent(es.findEvent(id)
                .orElseThrow(() -> new NotFoundException("Event Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createEvent(@Valid final EventForm form) {
        LOGGER.info("POST request arrived at neighborhoods/{}/events", neighborhoodId);
        final Event event = es.createEvent(form.getName(), form.getDescription(), form.getDate(), form.getStartTime(), form.getEndTime() , neighborhoodId);
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(event.getEventId())).build();
        return Response.created(uri).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response updateEventPartially(
            @PathParam("id") final long id,
            @Valid final EventForm partialUpdate) {
        LOGGER.info("PATCH request arrived at neighborhoods/{}/events/{}", neighborhoodId, id);
        final Event event = es.updateEventPartially(id, partialUpdate.getName(), partialUpdate.getDescription(), partialUpdate.getDate(), partialUpdate.getStartTime(), partialUpdate.getEndTime());
        return Response.ok(EventDto.fromEvent(event, uriInfo)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        LOGGER.info("DELETE request arrived at neighborhoods/{}/events/{}", neighborhoodId, id);
        es.deleteEvent(id);
        return Response.noContent().build();
    }
}
