package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Booking;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;
import java.util.List;

public class BookingDto {

    private Date bookingDate;
    private URI self;
    private URI user; // localhost:8080/users/{id}QUERYPARAM
    private URI amenityAvailability; // localhost:8080/amenities/{id}/availabilityQUERYPARAM

    // booking tiene un user y un availability (amenity y shift)

    public static BookingDto fromAmenity(Booking booking, UriInfo uriInfo){
        final BookingDto dto = new BookingDto();

        dto.bookingDate = booking.getBookingDate();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("bookings")
                .path(String.valueOf(booking.getBookingId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("users")
//                .queryParam("bookedAmenity", String.valueOf(user.getUserId())) //??????????????????????????????
                .build();
        dto.amenityAvailability = uriInfo.getBaseUriBuilder()
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
