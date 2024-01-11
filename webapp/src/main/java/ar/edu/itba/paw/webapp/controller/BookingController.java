package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.interfaces.services.UserService;
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
import java.sql.Date;
import java.util.List;

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
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findBooking(@PathParam("id") final long id) {
        LOGGER.info("Finding Booking with id {}", id);
        return Response.ok(BookingDto.fromBooking(bs.findBooking(id)
                .orElseThrow(() -> new NotFoundException("Booking Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createBooking(@Valid BookingForm form) {
        LOGGER.info("Creating Booking for Amenity {}", form.getAmenityId());
        final long[] bookingIds = bs.createBooking(getLoggedUser().getUserId(), form.getAmenityId(), form.getShiftIds(), form.getReservationDate());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(bookingIds)).build();
        return Response.created(uri).build();
    }

    // query params para filtrar segun userid o amenityid
}
