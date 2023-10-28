package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.MainEntities.Day;
import ar.edu.itba.paw.models.MainEntities.Shift;
import ar.edu.itba.paw.models.MainEntities.Time;
import ar.edu.itba.paw.models.MainEntities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class ShiftDaoImpl implements ShiftDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String SHIFTS =
            "SELECT s.*, d.dayname, t.timeinterval, d.dayid\n" +
                    "FROM shifts s\n" +
                    "INNER JOIN days d ON s.dayid = d.dayid\n" +
                    "INNER JOIN times t ON s.starttime = t.timeid ";
    private final RowMapper<Shift> ROW_MAPPER = (rs, rowNum) -> {
        return new Shift.Builder()
                .shiftId(rs.getLong("shiftid"))
                .startTime(
                        new Time.Builder()
                                .timeInterval(rs.getTime("timeinterval"))
                                .build()
                )
                .day(
                        new Day.Builder()
                                .dayName(rs.getString("dayname"))
                                .build()
                )
                .build();
    };
    private DayDao dayDao;
    private final RowMapper<Shift> ROW_MAPPER_2 = (rs, rowNum) -> {
        Day day = dayDao.findDayById(rs.getLong("dayid")).orElseThrow(() -> new NotFoundException("Day not found"));
        return new Shift.Builder()
                .shiftId(rs.getLong("shiftid"))
                .startTime(
                        new Time.Builder()
                                .timeInterval(rs.getTime("timeinterval"))
                                .build()
                )
                .day(day)
                .taken(rs.getBoolean("taken"))
                .build();
    };
    private TimeDao timeDao;

    // ----------------------------------------------- SHIFTS INSERT ---------------------------------------------------
    private String SHIFTS_JOIN_AVAILABILITY_SHIFTS =
            "SELECT s.shiftid, t.timeinterval, d.dayname, asa.amenityid\n" +
                    "FROM shifts s\n" +
                    "INNER JOIN amenities_shifts_availability asa ON asa.shiftid = s.shiftid\n" +
                    "INNER JOIN days d ON s.dayid = d.dayid\n" +
                    "INNER JOIN times t ON s.starttime = t.timeid ";

    // ----------------------------------------------- SHIFTS SELECT ---------------------------------------------------

    @Autowired
    public ShiftDaoImpl(final DataSource ds, final DayDao dayDao, final TimeDao timeDao) {
        this.timeDao = timeDao;
        this.dayDao = dayDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("shifts")
                .usingGeneratedKeyColumns("shiftid");
    }

    @Override
    public Shift createShift(long dayId, long startTimeId) {
        LOGGER.debug("Inserting Shift");
        Shift shift = new Shift.Builder()
                .day(em.find(Day.class, dayId))
                .startTime(em.find(Time.class, startTimeId))
                .build();
        em.persist(shift);
        return shift;
    }

    @Override
    public Optional<Shift> findShiftById(long shiftId) {
        LOGGER.debug("Selecting Shift with shiftId {}", shiftId);
        return Optional.ofNullable(em.find(Shift.class, shiftId));
    }

    @Override
    public Optional<Shift> findShiftId(long startTime, long dayId) {
        LOGGER.debug("Selecting Shift with startTime {} and dayId {}", startTime, dayId);
        final List<Shift> list = jdbcTemplate.query(SHIFTS + " WHERE s.starttime = ? and d.dayid = ?", ROW_MAPPER, startTime, dayId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Shift> getShifts(long amenityId, long dayId, Date date) {
        LOGGER.debug("Selecting Shifts with their status for the Amenity with amenityId {} for Day {} and Date {}", amenityId, dayId, date);
        String query =
                "SELECT s.*, t.timeinterval," +
                        "       CASE" +
                        "           WHEN (" +
                        "               a.amenityavailabilityid IN" +
                        "               (SELECT ua.amenityavailabilityid" +
                        "                FROM users_availability ua" +
                        "                WHERE date = ?)" +
                        "            )  THEN TRUE" +
                        "           ELSE FALSE" +
                        "       END AS taken" +
                        " FROM shifts s" +
                        " INNER JOIN times t ON s.starttime = t.timeid" +
                        " JOIN amenities_shifts_availability a ON s.shiftId = a.shiftId" +
                        " WHERE amenityid = ? AND dayid = ?;";
        return jdbcTemplate.query(query, ROW_MAPPER_2, date, amenityId, dayId);
    }

    @Override
    public List<Shift> getAmenityShifts(long amenityId) {
        LOGGER.debug("Selecting Weekly Available Shifts for Amenity {}", amenityId);
        return jdbcTemplate.query(SHIFTS_JOIN_AVAILABILITY_SHIFTS + " WHERE asa.amenityid = ?", ROW_MAPPER, amenityId);
    }
}