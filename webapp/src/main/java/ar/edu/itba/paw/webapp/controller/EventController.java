package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.EventDto;
import ar.edu.itba.paw.webapp.form.EventForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;


@Path("neighborhoods/{neighborhoodId}/events")
@Component
public class EventController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService es;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listEventsByDate(
            @QueryParam("forDate") final String date,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Event> events = es.getEvents(date, neighborhoodId, page, size);
        if(events.isEmpty())
            return Response.noContent().build();
        final List<EventDto> eventsDto = events.stream()
                .map(e -> EventDto.fromEvent(e, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/events",
                es.calculateEventPages(date, neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<EventDto>>(eventsDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findEvent(
            @PathParam("id") final long eventId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/events/{}'", neighborhoodId, eventId);

        // Content
        Event event = es.findEvent(eventId, neighborhoodId).orElseThrow(() -> new NotFoundException("Event Not Found"));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        EntityTag entityTag = new EntityTag(event.getVersion().toString());
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(EventDto.fromEvent(event, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response createEvent(
            @Valid final EventForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/events'", neighborhoodId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Creation & ETag Generation
        final Event event = es.createEvent(form.getName(), form.getDescription(), form.getDate(), form.getStartTime(), form.getEndTime() , neighborhoodId);
        entityLevelETag = ETagUtility.generateETag();

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(event.getEventId())).build();

        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response updateEventPartially(
            @PathParam("id") final long id,
            @Valid final EventForm partialUpdate,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/events/{}'", neighborhoodId, id);

        // Cache Control
        if (ifMatch != null) {
            String version = es.findEvent(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(version));
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, version)
                        .build();
        }

        // Modification & ETag Generation
        final EventDto eventDto = EventDto.fromEvent(es.updateEventPartially(id, partialUpdate.getName(), partialUpdate.getDescription(), partialUpdate.getDate(), partialUpdate.getStartTime(), partialUpdate.getEndTime()), uriInfo);
        entityLevelETag = ETagUtility.generateETag();

        return Response.ok(eventDto)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response deleteById(
            @PathParam("id") final long id,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/events/{}'", neighborhoodId, id);

        // Cache Control
        if (ifMatch != null) {
            String version = es.findEvent(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(version));
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, version)
                        .build();
        }

        // Deletion & ETag Generation Attempt
        if(es.deleteEvent(id)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
