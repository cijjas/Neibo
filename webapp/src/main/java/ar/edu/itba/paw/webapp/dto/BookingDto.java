package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.webapp.validation.constraints.form.AmenityURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.ReservationDateConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.ShiftURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.AmenityURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ShiftURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.UserURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class BookingDto {

    @NotNull(groups = OnCreate.class)
    @AmenityURNFormConstraint(groups = OnCreate.class)
    @AmenityURNReferenceConstraint(groups = OnCreate.class)
    private String amenity; // http://localhost:8080/neighborhoods/{neighborhoodId}/amenities/{amenityId}

    @NotNull(groups = OnCreate.class)
    @ShiftURNFormConstraint(groups = OnCreate.class)
    @ShiftURNReferenceConstraint(groups = OnCreate.class)
    private String shift; // http://localhost:8080/shifts/{shiftId}

    @NotNull(groups = OnCreate.class)
    @ReservationDateConstraint(groups = OnCreate.class)
    private String reservationDate;

    @NotNull(groups = OnCreate.class)
    @UserURNReferenceConstraint(groups = OnCreate.class)
    @UserURNCreateReferenceConstraint(groups = OnCreate.class)
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
