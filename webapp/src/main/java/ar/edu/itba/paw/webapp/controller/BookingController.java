package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.GroupedBooking;
import ar.edu.itba.paw.webapp.auth.UserAuth;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.BookingDto;
import ar.edu.itba.paw.webapp.form.AmenityForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.awt.print.Book;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Path("neighborhoods/{neighborhoodId}/bookings")
@Component
public class BookingController {
    @Autowired
    BookingService bs;

    @Autowired
    UserService us;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private String neighborhoodId;

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findBooking(@PathParam("id") final long id) {
        return Response.ok(BookingDto.fromBooking(bs.findBooking(id)
                .orElseThrow(() -> new NotFoundException("Booking Not Found")), uriInfo)).build();
    }

//    @POST
//    @Produces(value = { MediaType.APPLICATION_JSON, })
//    public Response createBooking(final Long amenityId, final List<Long> shiftIds, final Date reservationDate) {
//        final Booking booking = bs.createBooking(, amenityId, shiftIds);
//        final URI uri = uriInfo.getAbsolutePathBuilder()
//                .path(String.valueOf(booking.getBookingId())).build();
//        return Response.created(uri).build();
//    }

    // query params para filtrar segun userid o amenityid
}
