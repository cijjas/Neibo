package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.models.GroupedBooking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final BookingDao bookingDao;
    private final AvailabilityDao availabilityDao;

    @Autowired
    public BookingServiceImpl(final BookingDao bookingDao, final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
        this.bookingDao = bookingDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public long[] createBooking(long userId, long amenityId, List<Long> shiftIds, Date reservationDate) {
        LOGGER.info("Creating a Booking for Amenity {} on {} for User {}", amenityId, reservationDate, userId);

        List<Long> bookingIds = new ArrayList<>();

        for (Long shiftId : shiftIds) {
            long availabilityId = availabilityDao.findAvailabilityId(amenityId, shiftId)
                    .orElseThrow(() -> new NotFoundException("Availability not found.")); // DB guarantees the combination is unique

            Long bookingId = bookingDao.createBooking(userId, availabilityId, reservationDate).getBookingId();
            bookingIds.add(bookingId);
        }

        return bookingIds.stream()
                .mapToLong(Long::longValue)
                .toArray();
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findBooking(long bookingId){
        if (bookingId <= 0)
            throw new IllegalArgumentException("Booking ID must be a positive integer");
        return bookingDao.findBookingById(bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(long userId) {
        LOGGER.info("Getting Bookings for User {}", userId);
        List<Booking> userBookings = bookingDao.getUserBookings(userId);
        return userBookings;
//        List<GroupedBooking> groupedBookings = new ArrayList<>();
//        GroupedBooking currentGroupedBooking = null;
//
//        for (Booking booking : userBookings) {
//            if (currentGroupedBooking == null || !currentGroupedBooking.canCombine(booking)) {
//                // Create a new GroupedBooking when the current one cannot be continued
//                Time endTime = calculateEndTime(booking.getAmenityAvailability().getShift().getStartTime().getTimeInterval());
//                currentGroupedBooking = new GroupedBooking(
//                        booking.getAmenityAvailability().getAmenity().getName(),
//                        booking.getBookingDate(),
//                        booking.getAmenityAvailability().getShift().getDay().getDayName(),
//                        booking.getAmenityAvailability().getShift().getStartTime().getTimeInterval(),
//                        endTime
//                );
//                currentGroupedBooking.addBookingId(booking.getBookingId());
//                groupedBookings.add(currentGroupedBooking);
//            } else {
//                // Use the combine method to update the current GroupedBooking
//                Time endTime = calculateEndTime(booking.getAmenityAvailability().getShift().getStartTime().getTimeInterval());
//                currentGroupedBooking.combine(booking);
//                currentGroupedBooking.addBookingId(booking.getBookingId());
//            }
//        }
//
//        return groupedBookings;
    }


    private Time calculateEndTime(Time startTime) {
        // Calculate end time by adding an hour to the start time
        long startTimeMillis = startTime.getTime();
        long endTimeMillis = startTimeMillis + 60 * 60 * 1000; // 60 minutes * 60 seconds * 1000 milliseconds
        return new Time(endTimeMillis);
    }

    // -----------------------------------------------------------------------------------------------------------------

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
}
