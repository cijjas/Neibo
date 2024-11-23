package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.AmenityURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.ShiftURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.BookingDateConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.DateConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.Specific;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

@BookingDateConstraint
public class BookingDto {

    @NotNull(groups = Null.class)
    @AmenityURNConstraint(groups = URN.class)
    private String amenity; // http://localhost:8080/neighborhoods/{neighborhoodId}/amenities/{amenityId}

    @NotNull(groups = Null.class)
    @ShiftURNConstraint(groups = URN.class)
    private String shift; // http://localhost:8080/shifts/{shiftId}

    @NotNull(groups = Null.class)
    @DateConstraint(groups = Specific.class)
    private String reservationDate;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNCreateReferenceConstraint(groups = Authorization.class)
    private String user;

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

    public String getAmenity() {
        return amenity;
    }

    public void setAmenity(String amenity) {
        this.amenity = amenity;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

}
