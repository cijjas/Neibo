package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Booking;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class BookingDto {

    private Date bookingDate;
    private URI self;
    private URI user; // localhost:8080/users/{id}
    private URI amenityAvailability; // localhost:8080/neighborhoods/{id}/amenities/{id}/availabilityQUERYPARAM

    // booking tiene un user y un availability (amenity y shift)

    public static BookingDto fromBooking(Booking booking, UriInfo uriInfo){
        final BookingDto dto = new BookingDto();

        dto.bookingDate = booking.getBookingDate();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("bookings")
                .path(String.valueOf(booking.getBookingId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("users")
                .path(String.valueOf(booking.getUser().getUserId()))
//                .queryParam("bookedAmenity", String.valueOf(booking.getUser().getUserId()))
                .build();
        dto.amenityAvailability = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(booking.getAmenityAvailability().getAmenity().getNeighborhood().getNeighborhoodId()))
                .path("amenities")
                .path(String.valueOf(booking.getAmenityAvailability().getAmenity().getAmenityId()))
                .path("availability")
                .path(String.valueOf(booking.getAmenityAvailability().getAmenityAvailabilityId()))
                .build();

        return dto;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public URI getAmenityAvailability() {
        return amenityAvailability;
    }

    public void setAmenityAvailability(URI amenityAvailability) {
        this.amenityAvailability = amenityAvailability;
    }
}
