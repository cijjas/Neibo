package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.BookingDto;
import ar.edu.itba.paw.webapp.form.BookingForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/bookings")
@Component
public class BookingController extends GlobalControllerAdvice{
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private long neighborhoodId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @Autowired
    public BookingController(final UserService us) {super(us);}

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listBookings(
            @QueryParam("bookedBy") final Long userId,
            @QueryParam("forAmenity") final Long amenityId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/bookings'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Booking> bookings = bs.getBookings(userId, amenityId, neighborhoodId, page, size);
        if (bookings.isEmpty())
            return Response.noContent().build();
        final List<BookingDto> bookingsDto = bookings.stream()
                .map(b -> BookingDto.fromBooking(b, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/amenities",
                bs.calculateBookingPages(userId, amenityId, neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<BookingDto>>(bookingsDto){})
                .links(links)
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findBooking(
            @PathParam("id") final long bookingId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/bookings/{}'", neighborhoodId, bookingId);

        // Fetch
        Booking booking = bs.findBooking(bookingId, neighborhoodId).orElseThrow(() -> new NotFoundException("Booking Not Found"));

        // Check Caching
        EntityTag entityTag = new EntityTag(booking.getVersion().toString());
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Fresh Copy
        return Response.ok(BookingDto.fromBooking(booking, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createBooking(
            @Valid BookingForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/bookings'", neighborhoodId);

        // Check If-Match Header
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Usual Flow
        final long[] bookingIds = bs.createBooking(getLoggedUser().getUserId(), form.getAmenityURN(), form.getShiftURNs(), form.getReservationDate());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(Arrays.toString(bookingIds)).build();
        entityLevelETag = ETagUtility.generateETag();
        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(
            @PathParam("id") final long bookingId,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/bookings/{}'", neighborhoodId, bookingId);

        // Check If-Match header
        if (ifMatch != null) {
            String rowVersion = bs.findBooking(bookingId, neighborhoodId).orElseThrow(() -> new NotFoundException("Booking Not Found")).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(rowVersion));
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, rowVersion)
                        .build();
        }

        // Usual Flow
        if(bs.deleteBooking(bookingId)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
