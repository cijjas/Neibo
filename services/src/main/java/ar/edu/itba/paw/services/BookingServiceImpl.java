
package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.GroupedBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingDao bookingDao;
    private final AvailabilityDao availabilityDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    public BookingServiceImpl(final BookingDao bookingDao, final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
        this.bookingDao = bookingDao;
    }

    public long[] createBooking(long userId, long amenityId, List<Long> shiftIds, Date reservationDate) {
        LOGGER.info("Creating a Booking for Amenity {} on {} for User {}", amenityId, reservationDate, userId);

        List<Long> bookingIds = new ArrayList<>();

        for (Long shiftId : shiftIds) {
            Long availabilityId = availabilityDao.findAvailabilityId(amenityId, shiftId)
                    .orElseThrow(() -> new NotFoundException("Availability not found.")); // DB guarantees the combination is unique

            long bookingId = bookingDao.createBooking(userId, availabilityId, reservationDate).longValue();
            bookingIds.add(bookingId);
        }

        return bookingIds.stream()
                .mapToLong(Long::longValue)
                .toArray();
    }


    @Override
    public List<GroupedBooking> getUserBookings(long userId) {
        LOGGER.info("Getting Bookings for User {}", userId);
        List<Booking> userBookings = bookingDao.getUserBookings(userId);
        List<GroupedBooking> groupedBookings = new ArrayList<>();
        GroupedBooking currentGroupedBooking = null;

        for (Booking booking : userBookings) {
            if (currentGroupedBooking == null || !currentGroupedBooking.canCombine(booking)) {
                // Create a new GroupedBooking when the current one cannot be continued
                Time endTime = calculateEndTime(booking.getStartTime());
                currentGroupedBooking = new GroupedBooking(
                        booking.getAmenityName(),
                        booking.getBookingDate(),
                        booking.getDayName(),
                        booking.getStartTime(),
                        endTime
                );
                currentGroupedBooking.addBookingId(booking.getBookingId());
                groupedBookings.add(currentGroupedBooking);
            } else {
                // Use the combine method to update the current GroupedBooking
                Time endTime = calculateEndTime(booking.getStartTime());
                currentGroupedBooking.combine(booking);
                currentGroupedBooking.addBookingId(booking.getBookingId());
            }
        }

        return groupedBookings;
    }


    private Time calculateEndTime(Time startTime) {
        // Calculate end time by adding an hour to the start time
        long startTimeMillis = startTime.getTime();
        long endTimeMillis = startTimeMillis + 60 * 60 * 1000; // 60 minutes * 60 seconds * 1000 milliseconds
        return new Time(endTimeMillis);
    }

    @Override
    public boolean deleteBooking(long bookingId) {
        LOGGER.info("Deleting Booking {}", bookingId);
        return bookingDao.deleteBooking(bookingId);
    }

    @Override
    public boolean deleteBookings(List<Long> bookingIds) {
        LOGGER.info("Deleting Bookings {}", bookingIds);
        for (long booking : bookingIds)
            bookingDao.deleteBooking(booking);
        return true;
    }

    @Override
    public List<List<Booking>> getUserBookingsGroupedByAmenity(long userId) {
        return null;
    }
}
