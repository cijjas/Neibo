package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Booking;

import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class BookingDto {

    private Date bookingDate;
    private Links _links;

    public static BookingDto fromBooking(Booking booking, UriInfo uriInfo) {
        final BookingDto dto = new BookingDto();

        dto.bookingDate = booking.getBookingDate();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(booking.getUser().getNeighborhood().getNeighborhoodId()))
                .path("bookings")
                .path(String.valueOf(booking.getBookingId()))
                .build());
        links.setUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(booking.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(booking.getUser().getUserId()))
                .build());
        links.setShift(uriInfo.getBaseUriBuilder()
                .path("shifts")
                .path(String.valueOf(booking.getAmenityAvailability().getShift().getShiftId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}
