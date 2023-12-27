package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.models.GroupedBooking;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    long[] createBooking(long userId, long amenityId, List<Long> shiftIds, Date reservationDate);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Booking> findBooking(long bookingId);

    List<GroupedBooking> getUserBookings(long userId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteBooking(long bookingId);

    boolean deleteBookings(List<Long> bookingIds);
}
