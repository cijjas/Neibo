package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.enums.Table;
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
            "SELECT bookingid, date, a.amenityid, s.shiftid, userid, a.name, dayname, timeinterval\n" +
            "FROM users_availability uav\n" +
            "INNER JOIN amenities_shifts_availability asa ON uav.amenityavailabilityid = asa.amenityavailabilityid\n" +
            "INNER JOIN amenities a ON asa.amenityid = a.amenityid\n" +
            "INNER JOIN shifts s ON s.shiftid = asa.shiftid\n" +
            "INNER JOIN days d ON s.dayid = d.dayid\n" +
            "INNER JOIN times t ON s.starttime = t.timeid";

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingDaoImpl.class);

    @Autowired
    public BookingDaoImpl(final DataSource ds, final ShiftDao shiftDao, final AmenityDao amenityDao) {
        this.amenityDao = amenityDao;
        this.shiftDao = shiftDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(Table.users_availability.name())
                .usingGeneratedKeyColumns("bookingid");
    }

    // ---------------------------------------- USERS_AVAILABILITY CREATE ----------------------------------------------

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

    // ---------------------------------------- USERS_AVAILABILITY SELECT ----------------------------------------------

    private final RowMapper<Booking> ROW_MAPPER = (rs, rowNum) -> {
        return new Booking.Builder()
                .bookingId(rs.getLong("bookingid"))
                .userId(rs.getLong("userid"))
                .amenityName(rs.getString("name"))
                .bookingDate(rs.getDate("date"))
                .dayName(rs.getString("dayname"))
                .startTime(rs.getTime("timeinterval"))
                .build();
    };

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
