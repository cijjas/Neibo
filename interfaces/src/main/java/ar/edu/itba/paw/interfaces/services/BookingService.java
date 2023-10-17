package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.GroupedBooking;

import java.sql.Date;
import java.util.List;

public interface BookingService {

    long[] createBooking(long userId, long amenityId, List<Long> shiftIds, Date reservationDate);

    List<GroupedBooking> getUserBookings(long userId);

    List<List<Booking>> getUserBookingsGroupedByAmenity(long userId);
    boolean deleteBooking(long bookingId);

    boolean deleteBookings(List<Long> bookingIds);
}
