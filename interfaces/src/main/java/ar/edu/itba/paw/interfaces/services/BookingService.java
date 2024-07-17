package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Booking;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    long[] createBooking(long userId, String amenityId, List<String> shiftIds, String reservationDate);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Booking> findBooking(long bookingId, long neighborhoodId);

    List<Booking> getBookings(String userURN, String amenityURN, long neighborhoodId, int page, int size);

    // -----------------------------------------------------------------------------------------------------------------

    int calculateBookingPages(String userURN, String amenityURN, long neighborhoodId, int size);

    boolean deleteBooking(long bookingId);

    boolean deleteBookings(List<Long> bookingIds);
}
