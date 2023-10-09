package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Day;
import ar.edu.itba.paw.models.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BookingDaoImpl implements BookingDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private ShiftDao shiftDao;
    private AmenityDao amenityDao;

    private final String BOOKINGS_JOIN_AVAILABILITY =
            "select * " +
            "from users_availability uav " +
            "join amenities_shifts_availability asa " +
            "on uav.amenityavailabilityid = asa.amenityavailabilityid ";

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingDaoImpl.class);

    @Autowired
    public BookingDaoImpl(final DataSource ds, final ShiftDao shiftDao, final AmenityDao amenityDao) {
        this.amenityDao = amenityDao;
        this.shiftDao = shiftDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("users_availability")
                .usingGeneratedKeyColumns("bookingid");
    }

    // ----------------------------------------------- AMENITIES_SHIFTS INSERT -----------------------------------------
    // BOOKING
    @Override
    public void createBooking(long userId, long amenityAvailabilityId, Date reservationDate) {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", userId);
        data.put("amenityavailabilityid", amenityAvailabilityId);
        data.put("date", reservationDate);
        try {
            jdbcInsert.execute(data);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Booking", ex);
            throw new InsertionException("An error occurred whilst creating the Booking for the User");
        }
    }

    // ----------------------------------------------- AMENITIES_SHIFTS SELECT -----------------------------------------

    private final RowMapper<Booking> ROW_MAPPER = (rs, rowNum) -> {
        Shift shift = shiftDao.findShiftById(rs.getLong("shiftid")).orElseThrow(() -> new NotFoundException("Shift Not Found"));
        Amenity amenity = amenityDao.findAmenityById(rs.getLong("amenityid")).orElseThrow(() -> new NotFoundException("Amenity Not Found"));
        return new Booking.Builder()
                .bookingId(rs.getLong("bookingid"))
                .userId(rs.getLong("userid"))
                .bookingDate(rs.getDate("date"))
                .shift(shift)
                .amenity(amenity)
                .build();
    };

    @Override
    public List<Booking> getUserBookings(long userId) {
        return jdbcTemplate.query(BOOKINGS_JOIN_AVAILABILITY + " WHERE userid = ?", ROW_MAPPER, userId);
    }
}
