package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Booking;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking createBooking(long shiftId, long userId, long amenityId, Date reservationDate);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Booking> findBooking(long neighborhoodId, long bookingId);

    List<Booking> getBookings(long neighborhoodId, Long userId, Long amenityId, int page, int size);

    int countBookings(long neighborhoodId, Long amenityId, Long userId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteBooking(long neighborhoodId, long bookingId);
}
