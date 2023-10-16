package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.ReservationDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.Reservation;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.util.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReservationDaoImpl implements ReservationDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private UserDao userDao;
    private AmenityDao amenityDao;

    private final String RESERVATIONS = "SELECT * FROM reservations ";

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationDaoImpl.class);

    @Autowired
    public ReservationDaoImpl(final DataSource ds, UserDao userDao, AmenityDao amenityDao) {
        this.userDao = userDao;
        this.amenityDao = amenityDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("reservationid")
                .withTableName("reservations");
    }

    // ---------------------------------------------- RESERVATIONS INSERT ----------------------------------------------

    @Override
    public Reservation createReservation(long amenityId, long userId, Date date, Time startTime, Time endTime) {
        Map<String, Object> data = new HashMap<>();
        data.put("amenityid", amenityId);
        data.put("userid", userId);
        data.put("date", date);
        data.put("starttime", startTime);
        data.put("endtime", endTime);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Reservation.Builder()
                    .reservationId(key.longValue())
                    .date(date)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Reservation", ex);
            throw new InsertionException("An error occurred whilst creating the Reservation");
        }

    }

    // ---------------------------------------------- RESERVATIONS SELECT ----------------------------------------------

    private final RowMapper<Reservation> ROW_MAPPER = (rs, rowNum) -> {
        User user = userDao.findUserById(rs.getLong("userid")).orElse(null);
        Amenity amenity = amenityDao.findAmenityById(rs.getLong("amenityid")).orElse(null);

        return new Reservation.Builder()
                .reservationId(rs.getLong("reservationid"))
                .date(rs.getDate("date"))
                .startTime(rs.getTime("starttime"))
                .endTime(rs.getTime("endtime"))
                .user(user)
                .amenity(amenity)
                .build();
    };

    @Override
    public Reservation findReservationById(long reservationId) {
        return jdbcTemplate.queryForObject(RESERVATIONS + " WHERE reservationid = ?", ROW_MAPPER, reservationId);
    }

    @Override
    public List<Reservation> getReservations() {
        return jdbcTemplate.query("SELECT * FROM reservations", ROW_MAPPER);
    }

    @Override
    public List<Reservation> getReservationsByAmenityId(long amenityId) {
        return jdbcTemplate.query(RESERVATIONS + " WHERE amenityid = ?", ROW_MAPPER, amenityId);
    }

    @Override
    public List<Reservation> getReservationsByUserId(long userId) {
        return jdbcTemplate.query(RESERVATIONS + " WHERE userid = ?", ROW_MAPPER, userId);
    }

    @Override
    public List<Reservation> getReservationsByDay(long amenityId, Date date) {
        return jdbcTemplate.query(RESERVATIONS + " WHERE amenityid = ? AND date = ?", ROW_MAPPER, amenityId, date);
    }

    // ---------------------------------------------- RESERVATIONS DELETE ----------------------------------------------

    @Override
    public boolean deleteReservation(long reservationId) {
        return jdbcTemplate.update("DELETE FROM reservations WHERE reservationid = ?", reservationId) > 0;
    }

}
