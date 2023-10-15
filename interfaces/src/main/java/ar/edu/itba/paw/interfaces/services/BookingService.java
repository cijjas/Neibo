package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Booking;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public interface BookingService {

    void createBooking(long userId, long amenityId, List<Long> shiftIds, Date reservationDate);

    List<Booking> getUserBookings(long userId);

    boolean deleteBooking(long bookingId);
    public List<List<Booking>> getUserBookingsGroupedByAmenity(long userId);
}
