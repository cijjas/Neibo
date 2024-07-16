package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Booking;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class BookingDto {

    private Date bookingDate;
    private URI self;
    private URI user; // localhost:8080/users/{id}
    private URI shiftURN;

    public static BookingDto fromBooking(Booking booking, UriInfo uriInfo){
        final BookingDto dto = new BookingDto();

        dto.bookingDate = booking.getBookingDate();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(booking.getUser().getNeighborhood().getNeighborhoodId()))
                .path("bookings")
                .path(String.valueOf(booking.getBookingId()))
                .build();

        dto.user = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(booking.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(booking.getUser().getUserId()))
                .build();

        dto.shiftURN =  uriInfo.getBaseUriBuilder()
                .path("shifts")
                .path(String.valueOf(booking.getAmenityAvailability().getShift().getShiftId()))
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

    public URI getShiftURN() {
        return shiftURN;
    }

    public void setShiftURN(URI shiftURN) {
        this.shiftURN = shiftURN;
    }
}
