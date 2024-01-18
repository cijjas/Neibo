package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.webapp.dto.BookingDto;
import ar.edu.itba.paw.webapp.form.BookingForm;
import ar.edu.itba.paw.exceptions.NotFoundException;
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

@Path("neighborhoods/{neighborhoodId}/bookings")
@Component
public class BookingController extends GlobalControllerAdvice{
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bs;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private String neighborhoodId;

    @Autowired
    public BookingController(final UserService us, final BookingService bs) {
        super(us);
        this.bs = bs;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listBookings(
            @QueryParam("userId") final Long userId,
            @QueryParam("amenityId") final Long amenityId
    ) {
        LOGGER.info("GET request arrived at neighborhoods/{}/bookings", neighborhoodId);
        final List<Booking> bookings = bs.getBookings(userId, amenityId);
        final List<BookingDto> bookingsDto = bookings.stream()
                .map(b -> BookingDto.fromBooking(b, uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<BookingDto>>(bookingsDto){})
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findBooking(@PathParam("id") final long id) {
        LOGGER.info("GET request arrived at neighborhoods/{}/bookings/{}", neighborhoodId, id);
        return Response.ok(BookingDto.fromBooking(bs.findBooking(id)
                .orElseThrow(() -> new NotFoundException("Booking Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createBooking(@Valid BookingForm form) {
        LOGGER.info("POST request arrived at neighborhoods/{}/bookings", neighborhoodId);
        final long[] bookingIds = bs.createBooking(getLoggedUser().getUserId(), form.getAmenityId(), form.getShiftIds(), form.getReservationDate());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(bookingIds)).build();
        return Response.created(uri).build();
    }
}
