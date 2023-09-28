package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.ReservationDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.Reservation;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
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

    @Autowired
    public ReservationDaoImpl(final DataSource ds, UserDao userDao, AmenityDao amenityDao) {
        this.userDao = userDao;
        this.amenityDao = amenityDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("reservationid")
                .withTableName("reservation");
    }

    @Override
    public Reservation createReservation(long amenityId, long userId, Date date, Time startTime, Time endTime) {
        Map<String, Object> data = new HashMap<>();
        data.put("amenityid", amenityId);
        data.put("userid", userId);
        data.put("date", date);
        data.put("starttime", startTime);
        data.put("endtime", endTime);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Reservation.Builder()
                .reservationId(key.longValue())
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

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
    public List<Reservation> getReservations() {
        return jdbcTemplate.query("SELECT * FROM reservation", ROW_MAPPER);
    }

    @Override
    public Reservation findReservationById(long reservationId) {
        return jdbcTemplate.queryForObject("SELECT * FROM reservation WHERE reservationid = ?", ROW_MAPPER, reservationId);
    }

    @Override
    public List<Reservation> getReservationsByAmenityId(long amenityId) {
        return jdbcTemplate.query("SELECT * FROM reservation WHERE amenityid = ?", ROW_MAPPER, amenityId);
    }

    @Override
    public List<Reservation> getReservationsByUserId(long userId) {
        return jdbcTemplate.query("SELECT * FROM reservation WHERE userid = ?", ROW_MAPPER, userId);
    }

    @Override
    public List<Reservation> getReservationsByDay(long amenityId, Date date) {
        return jdbcTemplate.query("SELECT * FROM reservation WHERE amenityid = ? AND date = ?", ROW_MAPPER, amenityId, date);
    }

    @Override
    public boolean deleteReservation(long reservationId) {
        return jdbcTemplate.update("DELETE FROM reservation WHERE reservationid = ?", reservationId) > 0;
    }

}
