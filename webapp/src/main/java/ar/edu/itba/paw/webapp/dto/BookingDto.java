package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceInCreationConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.AmenityURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ShiftURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.BookingDateConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.DateConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.Specific;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@BookingDateConstraint
public class BookingDto {

    @NotNull(groups = Null.class)
    @AmenityURNConstraint(groups = URN.class)
    private String amenity;

    @NotNull(groups = Null.class)
    @ShiftURNConstraint(groups = URN.class)
    private String shift;

    @NotNull(groups = Null.class)
    @DateConstraint(groups = Specific.class)
    private String bookingDate;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNReferenceInCreationConstraint(groups = Authorization.class)
    private String user;

    private Links _links;

    public static BookingDto fromBooking(Booking booking, UriInfo uriInfo) {
        final BookingDto dto = new BookingDto();

        dto.bookingDate = booking.getBookingDate().toString();

        Links links = new Links();

        String neighborhoodId = String.valueOf(booking.getUser().getNeighborhood().getNeighborhoodId());
        String shiftId = String.valueOf(booking.getAmenityAvailability().getShift().getShiftId());
        String amenityId = String.valueOf(booking.getAmenityAvailability().getAmenity().getAmenityId());
        String userId = String.valueOf(booking.getUser().getUserId());
        String bookingId = String.valueOf(booking.getBookingId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString()).path(neighborhoodId);
        UriBuilder shiftUri = uriInfo.getBaseUriBuilder().path(Endpoint.SHIFTS.toString()).path(shiftId);
        UriBuilder bookingUri = neighborhoodUri.clone().path(Endpoint.BOOKINGS.toString()).path(bookingId);
        UriBuilder userUri = neighborhoodUri.clone().path(Endpoint.USERS.toString()).path(userId);
        UriBuilder amenityUri = neighborhoodUri.clone().path(Endpoint.AMENITIES.toString()).path(amenityId);

        links.setSelf(bookingUri.build());
        links.setBookingUser(userUri.build());
        links.setAmenity(amenityUri.build());
        links.setShift(shiftUri.build());

        dto.set_links(links);
        return dto;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
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

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
}
