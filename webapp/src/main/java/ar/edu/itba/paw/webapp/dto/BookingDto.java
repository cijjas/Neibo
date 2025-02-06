package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.BookingDateConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.DateConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.Specific;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@BookingDateConstraint(groups = Specific.class)
public class BookingDto {

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.AMENITY_URI_REGEX)
    private String amenity;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.SHIFT_URI_REGEX)
    private String shift;

    @NotNull(groups = OnCreate.class)
    @DateConstraint(groups = Specific.class)
    private String bookingDate;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
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

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder shiftUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.SHIFTS).path(shiftId);
        UriBuilder bookingUri = neighborhoodUri.clone().path(Endpoint.BOOKINGS).path(bookingId);
        UriBuilder userUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USERS).path(userId);
        UriBuilder amenityUri = neighborhoodUri.clone().path(Endpoint.AMENITIES).path(amenityId);

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
