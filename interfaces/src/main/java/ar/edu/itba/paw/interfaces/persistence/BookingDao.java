package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Booking;

import java.sql.Date;
import java.util.List;

public interface BookingDao {

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY DELETE -----------------------------------------

    Number createBooking(long userId, long amenityAvailabilityId, Date reservationDate);

    // ----------------------------------------------- AMENITIES_SHIFTS SELECT -----------------------------------------

    List<Booking> getUserBookings(long userId);

    boolean deleteBooking(long bookingId);
}
