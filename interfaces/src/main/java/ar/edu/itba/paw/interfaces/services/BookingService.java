package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking createBooking(long userId, long amenityId, long shiftId, String reservationDate);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Booking> findBooking(long bookingId, long neighborhoodId);

    List<Booking> getBookings(String userURN, String amenityURN, long neighborhoodId, int page, int size);

    // ---------------------------------------------------

    int calculateBookingPages(String userURN, String amenityURN, long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteBooking(long bookingId);
}
