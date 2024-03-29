package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.*;
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
    private final AmenityDao amenityDao;
    private final ShiftDao shiftDao;

    @Autowired
    public BookingServiceImpl(final BookingDao bookingDao, final AvailabilityDao availabilityDao, final NeighborhoodDao neighborhoodDao, final AmenityDao amenityDao, final ShiftDao shiftDao) {
        this.availabilityDao = availabilityDao;
        this.bookingDao = bookingDao;
        this.neighborhoodDao = neighborhoodDao;
        this.amenityDao = amenityDao;
        this.shiftDao = shiftDao;
    }

    public long[] createBooking(long userId, String amenityURN, List<String> shiftURNs, String reservationDate) {
        LOGGER.info("Creating a Booking for Amenity {} on Date {} for User {}", amenityURN, reservationDate, userId);

        // amenityURN = http://localhost:8080/neighborhoods/{neighborhoodId}/amenities/{amenityId}
        // extract neighborhoodId and amenityId from amenityURN
        // ValidationUtils.checkValidNeighborhoodId(neighborhoodId)
        // ValidationUtils.checkValidAmenityId(amenityId)
        // amenityDao.findAmenity(neighborhoodId, amenityId)
        // Extracting neighborhoodId and amenityId from amenityURN

        // shiftsURN = { http://localhost:8080/shifts/{shiftId}, http://localhost:8080/shifts/{shiftId}, http://localhost:8080/shifts/{shiftId}}
        // extract shiftsIds
        // ValidationUtils.checkValidShiftId(shiftId)
        // cycle that makes ShiftDao.findShift(shiftId)

        String[] amenityParts = amenityURN.split("/");
        if (amenityParts.length < 6) {
            throw new IllegalArgumentException("Invalid amenity URN format.");
        }
        long neighborhoodId = Long.parseLong(amenityParts[4]);
        long amenityId = Long.parseLong(amenityParts[6]);

        // Validating neighborhoodId and amenityId
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkAmenityId(amenityId);
        amenityDao.findAmenity(amenityId, neighborhoodId).orElseThrow(NotFoundException::new);

        List<Long> bookingIds = new ArrayList<>();

        // Check Date Format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(reservationDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid Date Format. Please use YYYY-MM-DD.");
        }
        java.sql.Date parsedSqlDate = new java.sql.Date(parsedDate.getTime());


        for (String shiftURN : shiftURNs) {
            // Extracting shiftId from shiftURN
            String[] shiftParts = shiftURN.split("/");
            if (shiftParts.length < 5 || !"shifts".equals(shiftParts[3])) {
                throw new IllegalArgumentException("Invalid shift URN format.");
            }
            long shiftId = Long.parseLong(shiftParts[4]);

            // Validating shiftId and Shift existence
            ValidationUtils.checkShiftId(shiftId);
            shiftDao.findShift(shiftId).orElseThrow(NotFoundException::new);

            // Finding availabilityId using amenityId and shiftId
            long availabilityId = availabilityDao.findId(amenityId, shiftId).orElseThrow(() -> new NotFoundException("Availability not found."));

            // Creating booking
            Long bookingId = bookingDao.createBooking(userId, availabilityId, parsedSqlDate).getBookingId();
            bookingIds.add(bookingId);
        }

        return bookingIds.stream()
                .mapToLong(Long::longValue)
                .toArray();
    }

    // -----------------------------------------------------------------------------------------------------------------

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
