package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.models.Entities.Booking;
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

    @Autowired
    public BookingServiceImpl(final BookingDao bookingDao, final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
        this.bookingDao = bookingDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Booking createBooking(long user, long amenity, long shift, Date reservationDate) {
        LOGGER.info("Creating a Booking for Amenity {} on Date {} for User {}", amenity, reservationDate, user);

        Availability availability = availabilityDao.findAvailability(amenity, shift).orElseThrow(NotFoundException::new);

        return bookingDao.createBooking(user, availability.getAmenityAvailabilityId(), reservationDate);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findBooking(long bookingId, long neighborhoodId) {
        LOGGER.info("Finding Booking {}", bookingId);

        return bookingDao.findBooking(bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookings(Long userId, Long amenityId, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Bookings for User {} on Amenity {} from Neighborhood {}", userId, amenityId, neighborhoodId);

        return bookingDao.getBookings(userId, amenityId, neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateBookingPages(Long userId, Long amenityId, long neighborhoodId, int size) {
        LOGGER.info("Calculating Booking Pages for User {} on Amenity {} from Neighborhood {}", userId, amenityId, neighborhoodId);

        return PaginationUtils.calculatePages(bookingDao.countBookings(userId, amenityId, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteBooking(long bookingId) {
        LOGGER.info("Deleting Booking {}", bookingId);

        return bookingDao.deleteBooking(bookingId);
    }
}