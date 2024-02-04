package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.models.Entities.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hibernate.type.descriptor.java.JdbcDateTypeDescriptor.DATE_FORMAT;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final BookingDao bookingDao;
    private final AvailabilityDao availabilityDao;
    private final NeighborhoodDao neighborhoodDao;

    @Autowired
    public BookingServiceImpl(final BookingDao bookingDao, final AvailabilityDao availabilityDao, final NeighborhoodDao neighborhoodDao) {
        this.availabilityDao = availabilityDao;
        this.bookingDao = bookingDao;
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public long[] createBooking(long userId, long amenityId, List<Long> shiftIds, String reservationDate) {
        LOGGER.info("Creating a Booking for Amenity {} on Date {} for User {}", amenityId, reservationDate, userId);

        List<Long> bookingIds = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(reservationDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid Date Format. Please use YYYY-MM-DD.");
        }
        java.sql.Date parsedSqlDate = new java.sql.Date(parsedDate.getTime());

        for (Long shiftId : shiftIds) {
            long availabilityId = availabilityDao.findId(amenityId, shiftId)
                    .orElseThrow(() -> new NotFoundException("Availability not found.")); // DB guarantees the combination is unique

            Long bookingId = bookingDao.createBooking(userId, availabilityId, parsedSqlDate).getBookingId();
            bookingIds.add(bookingId);
        }

        return bookingIds.stream()
                .mapToLong(Long::longValue)
                .toArray();
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findBooking(long bookingId, long neighborhoodId){
        LOGGER.info("Finding Booking {}", bookingId);

        ValidationUtils.checkBookingId(bookingId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return bookingDao.findBooking(bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookings(Long userId, Long amenityId, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Bookings for User {} on Amenity {} from Neighborhood {}", userId, amenityId, neighborhoodId);

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkAmenityId(amenityId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return bookingDao.getBookings(userId, amenityId, page, size);
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

    @Override
    public int calculateBookingPages(Long userId, Long amenityId, long neighborhoodId, int size) {
        LOGGER.info("Calculating Booking Pages for User {} on Amenity {} from Neighborhood {}", userId, amenityId, neighborhoodId);

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkAmenityId(amenityId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return PaginationUtils.calculatePages(bookingDao.countBookings(userId, amenityId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteBooking(long bookingId) {
        LOGGER.info("Deleting Booking {}", bookingId);

        ValidationUtils.checkBookingId(bookingId);

        return bookingDao.deleteBooking(bookingId);
    }

    @Override
    public boolean deleteBookings(List<Long> bookingIds) {
        LOGGER.info("Deleting Bookings {}", bookingIds);

        boolean result = true;
        for (long bookingId : bookingIds) {
            ValidationUtils.checkBookingId(bookingId);
            if (!deleteBooking(bookingId))
                result = false;
        }
        return result;
    }
}
