package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.models.TwoId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    // -----------------------------------------------------------------------------------------------------------------

    public Booking createBooking(long user, long amenity, long shift, String reservationDate) {
        LOGGER.info("Creating a Booking for Amenity {} on Date {} for User {}", amenity, reservationDate, user);

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

        Availability availability = availabilityDao.findAvailability(amenity, shift).orElseThrow(() -> new NotFoundException("Availability not found."));

        return bookingDao.createBooking(user, availability.getAmenityAvailabilityId(), parsedSqlDate);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findBooking(long bookingId, long neighborhoodId) {
        LOGGER.info("Finding Booking {}", bookingId);

        ValidationUtils.checkBookingId(bookingId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return bookingDao.findBooking(bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookings(String userURN, String amenityURN, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Bookings for User {} on Amenity {} from Neighborhood {}", userURN, amenityURN, neighborhoodId);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);
        Long amenityId = ValidationUtils.checkURNAndExtractAmenityId(amenityURN);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return bookingDao.getBookings(userId, amenityId, page, size);
    }

    // ---------------------------------------------------

    @Override
    public int calculateBookingPages(String userURN, String amenityURN, long neighborhoodId, int size) {
        LOGGER.info("Calculating Booking Pages for User {} on Amenity {} from Neighborhood {}", userURN, amenityURN, neighborhoodId);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);
        Long amenityId = ValidationUtils.checkURNAndExtractAmenityId(amenityURN);

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
}