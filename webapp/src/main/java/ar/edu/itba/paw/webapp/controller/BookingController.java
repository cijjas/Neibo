package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Booking;
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

    private final BookingService bs;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private long neighborhoodId;

    @Autowired
    public BookingController(final UserService us, final BookingService bs) {
        super(us);
        this.bs = bs;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listBookings(
            @QueryParam("bookedBy") final Long userId,
            @QueryParam("forAmenity") final Long amenityId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/bookings'", neighborhoodId);
        final List<Booking> bookings = bs.getBookings(userId, amenityId, neighborhoodId, page, size);

        if (bookings.isEmpty())
            return Response.noContent().build();

        final List<BookingDto> bookingsDto = bookings.stream()
                .map(b -> BookingDto.fromBooking(b, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/amenities";
        int totalBookingPages = bs.calculateBookingPages(userId, amenityId, neighborhoodId, size);
        LOGGER.info("totalBookingPages: {}", totalBookingPages);
        Link[] links = createPaginationLinks(baseUri, page, size, totalBookingPages);

        return Response.ok(new GenericEntity<List<BookingDto>>(bookingsDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findBooking(@PathParam("id") final long bookingId) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/bookings/{}'", neighborhoodId, bookingId);
        return Response.ok(BookingDto.fromBooking(bs.findBooking(bookingId, neighborhoodId)
                .orElseThrow(() -> new NotFoundException("Booking Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createBooking(@Valid BookingForm form) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/bookings'", neighborhoodId);
        final long[] bookingIds = bs.createBooking(getLoggedUser().getUserId(), form.getAmenityId(), form.getShiftIds(), form.getReservationDate());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(Arrays.toString(bookingIds)).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/bookings/{}'", neighborhoodId, id);
        if(bs.deleteBooking(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
