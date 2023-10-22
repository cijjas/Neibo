package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.MainEntities.Amenity;
import ar.edu.itba.paw.models.JunctionEntities.Booking;
import ar.edu.itba.paw.models.MainEntities.Day;
import ar.edu.itba.paw.models.MainEntities.Time;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private ShiftDao shiftDao;
    private AmenityDao amenityDao;
    private DayDao dayDao;
    private TimeDao timeDao;

    private final String BOOKINGS_JOIN_AVAILABILITY =
            "SELECT bookingid, date, a.amenityid, s.shiftid, userid, a.name, d.dayid, t.timeid, timeinterval\n" +
                    "FROM users_availability uav\n" +
                    "INNER JOIN amenities_shifts_availability asa ON uav.amenityavailabilityid = asa.amenityavailabilityid\n" +
                    "INNER JOIN amenities a ON asa.amenityid = a.amenityid\n" +
                    "INNER JOIN shifts s ON s.shiftid = asa.shiftid\n" +
                    "INNER JOIN days d ON s.dayid = d.dayid\n" +
                    "INNER JOIN times t ON s.starttime = t.timeid";
    private final RowMapper<Booking> ROW_MAPPER = (rs, rowNum) -> {
        Amenity amenity = amenityDao.findAmenityById(rs.getLong("amenityid")).orElseThrow(()-> new NotFoundException("Amenity Not Found"));
        Day day = dayDao.findDayById(rs.getLong("dayid")).orElseThrow(()-> new NotFoundException("Day Not Found"));
        Time time = timeDao.findTimeById(rs.getLong("timeid")).orElseThrow(()-> new NotFoundException("Time Not Found"));
        return new Booking.Builder()
                .bookingId(rs.getLong("bookingid"))
                .userId(rs.getLong("userid"))
                .amenity(amenity)
                .bookingDate(rs.getDate("date"))
                .day(day)
                .time(time)
                .build();
    };


    // ---------------------------------------- USERS_AVAILABILITY CREATE ----------------------------------------------

    @Autowired
    public BookingDaoImpl(final DataSource ds, final ShiftDao shiftDao, final AmenityDao amenityDao, final DayDao dayDao, final TimeDao timeDao) {
        this.timeDao = timeDao;
        this.amenityDao = amenityDao;
        this.dayDao = dayDao;
        this.shiftDao = shiftDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(Table.users_availability.name())
                .usingGeneratedKeyColumns("bookingid");
    }

    // ---------------------------------------- USERS_AVAILABILITY SELECT ----------------------------------------------

    @Override
    public Number createBooking(long userId, long amenityAvailabilityId, Date reservationDate) {
        LOGGER.debug("Inserting Booking");
        Map<String, Object> data = new HashMap<>();
        data.put("userid", userId);
        data.put("amenityavailabilityid", amenityAvailabilityId);
        data.put("date", reservationDate);
        try {
            return jdbcInsert.executeAndReturnKey(data);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Booking", ex);
            throw new InsertionException("An error occurred whilst creating the Booking for the User");
        }
    }

    @Override
    public List<Booking> getUserBookings(long userId) {
        LOGGER.debug("Selecting Bookings from userId {}", userId);
        return jdbcTemplate.query(BOOKINGS_JOIN_AVAILABILITY + " WHERE userid = ? ORDER BY uav.date, asa.amenityid, asa.shiftid;", ROW_MAPPER, userId);
    }

    // ---------------------------------------- USERS_AVAILABILITY DELETE ----------------------------------------------

    @Override
    public boolean deleteBooking(long bookingId) {
        LOGGER.debug("Deleting Booking with bookingId {}", bookingId);
        return jdbcTemplate.update("DELETE FROM users_availability WHERE bookingid = ? ", bookingId) > 0;
    }
}
