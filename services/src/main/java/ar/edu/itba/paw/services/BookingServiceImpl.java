package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.models.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingDao bookingDao;
    private final AvailabilityDao availabilityDao;

    @Autowired
    public BookingServiceImpl(final BookingDao bookingDao, final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
        this.bookingDao = bookingDao;
    }

    public void createBooking(long userId,long amenityId, List<Long> shiftIds,  Date reservationDate) {
        for (Long shiftId : shiftIds) {
            Long availabilityId = availabilityDao.findAvailabilityId(amenityId, shiftId).orElseThrow(()-> new NotFoundException("Availability not found.")); // DB guarantees the combination is unique
            bookingDao.createBooking(userId, availabilityId, reservationDate);
        }
    }

    @Override
    public List<Booking> getUserBookings(long userId){
        return bookingDao.getUserBookings(userId);
    }

    @Override
    public boolean deleteBooking(long bookingId) {
        return bookingDao.deleteBooking(bookingId);
    }
}
