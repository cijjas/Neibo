package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Booking;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking createBooking(long userId, long amenityId, long shiftId, Date reservationDate);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Booking> findBooking(long bookingId, long neighborhoodId);

    List<Booking> getBookings(Long userId, Long amenityId, long neighborhoodId, int page, int size);

    int calculateBookingPages(Long userId, Long amenityId, long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteBooking(long bookingId);
}
