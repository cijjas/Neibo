package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Booking;

import java.sql.Date;
import java.util.List;

public interface BookingDao {
    void createBooking(long userId, long amenityAvailabilityId, Date reservationDate);

    List<Booking> getUserBookings(long userId);
}
