package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Booking;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface BookingDao {

    // ------------------------------------------------- BOOKINGS INSERT -----------------------------------------------

    Booking createBooking(long userId, long amenityAvailabilityId, Date reservationDate);

    // ------------------------------------------------- BOOKINGS SELECT -----------------------------------------------

    Optional<Booking> findBookingById(long bookingId);

    List<Booking> getUserBookings(long userId);

    // ------------------------------------------------- BOOKINGS DELETE -----------------------------------------------

    boolean deleteBooking(long bookingId);
}
