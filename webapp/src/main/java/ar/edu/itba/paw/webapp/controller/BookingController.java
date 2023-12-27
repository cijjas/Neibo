package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.models.GroupedBooking;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.BookingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

@Path("neighborhoods/{neighborhoodId}/bookings")
@Component
public class BookingController {
    @Autowired
    BookingService bs;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private String neighborhoodId;

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findBooking(@PathParam("id") final long id) {
        Optional<Booking> booking = bs.findBooking(id);
        if (!booking.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(BookingDto.fromBooking(booking.get(), uriInfo)).build();
    }

    // query params para filtrar segun userid o amenityid
}
