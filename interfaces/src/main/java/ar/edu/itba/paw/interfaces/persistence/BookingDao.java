package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Booking;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BookingDao {

    // ------------------------------------------------- BOOKINGS INSERT -----------------------------------------------

    Booking createBooking(long userId, long amenityAvailabilityId, Date reservationDate);

    // ------------------------------------------------- BOOKINGS SELECT -----------------------------------------------

    Optional<Booking> findBooking(long bookingId);

    List<Booking> getBookings(long neighborhoodId, Long userId, Long amenityId, int page, int size);

    // ------------------------------------------------- BOOKINGS DELETE -----------------------------------------------

    int countBookings(long neighborhoodId, Long userId, Long amenityId);

    boolean deleteBooking(long neighborhoodId, long bookingId);
}
