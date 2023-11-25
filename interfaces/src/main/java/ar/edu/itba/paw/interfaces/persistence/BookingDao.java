package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Booking;

import java.sql.Date;
import java.util.List;

public interface BookingDao {

    // ------------------------------------------------- BOOKINGS INSERT -----------------------------------------------

    Booking createBooking(long userId, long amenityAvailabilityId, Date reservationDate);

    // ------------------------------------------------- BOOKINGS SELECT -----------------------------------------------

    List<Booking> getUserBookings(long userId);

    // ------------------------------------------------- BOOKINGS DELETE -----------------------------------------------

    boolean deleteBooking(long bookingId);
}
