package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.*;
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
import java.util.Optional;

@Repository
public class ShiftDaoImpl implements ShiftDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private DayDao dayDao;
    private TimeDao timeDao;

    private final String SHIFTS = "SELECT * FROM shifts ";
    private String SHIFTS_JOIN_AVAILABILITY_SHIFTS =
            "select *\n" +
                    "from amenities_shifts_availability av\n" +
                    "join shifts s on av.shiftid = av.shiftid\n" +
                    "join times t on s.starttime = t.timeid\n" +
                    "join days d on s.dayid = d.dayid";

    private final String SHIFTS_JOIN_AMENITIES =
            "SELECT s.* " +
            "FROM shifts s " +
            "JOIN amenities_shifts_availability a ON s.shiftid = a.shiftid ";

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftDaoImpl.class);

    @Autowired
    public ShiftDaoImpl(final DataSource ds, final DayDao dayDao, final TimeDao timeDao) {
        this.timeDao = timeDao;
        this.dayDao = dayDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("shifts")
                .usingGeneratedKeyColumns("shiftid");
    }

    // ----------------------------------------------- SHIFTS INSERT ---------------------------------------------------

    @Override
    public Shift createShift(long dayId, long startTimeId) {
        Map<String, Object> data = new HashMap<>();
        data.put("dayid", dayId);
        data.put("starttime", startTimeId);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Shift.Builder()
                    .shiftId(key.longValue())
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Shift", ex);
            throw new InsertionException("An error occurred whilst creating the shift corresponding to the Amenity");
        }
    }

    // ----------------------------------------------- SHIFTS SELECT ---------------------------------------------------

    private final RowMapper<Shift> ROW_MAPPER = (rs, rowNum) -> {
        Day day =  dayDao.findDayById(rs.getLong("dayid")).orElseThrow(()-> new NotFoundException("Day not found"));
        Time startTime =  timeDao.findTimeById(rs.getLong("starttime")).orElseThrow(()-> new NotFoundException("Time not found"));
        return new Shift.Builder()
                .shiftId(rs.getLong("shiftid"))
                .day(day)
                .startTime(startTime)
                .build();
    };

    private final RowMapper<Shift> ROW_MAPPER_2 = (rs, rowNum) -> {
        Day day =  dayDao.findDayById(rs.getLong("dayid")).orElseThrow(()-> new NotFoundException("Day not found"));
        Time startTime =  timeDao.findTimeById(rs.getLong("starttime")).orElseThrow(()-> new NotFoundException("Time not found"));
        return new Shift.Builder()
                .shiftId(rs.getLong("shiftid"))
                .day(day)
                .startTime(startTime)
                .taken(rs.getBoolean("taken"))
                .build();
    };

    @Override
    public Optional<Shift> findShiftById(long shiftId) {
        final List<Shift> list = jdbcTemplate.query(SHIFTS + " WHERE shiftid = ?", ROW_MAPPER, shiftId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }


    @Override
    public List<Shift> getShifts(long amenityId, long dayId, Date date) {
        String query =
                "SELECT s.*," +
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
                " JOIN amenities_shifts_availability a ON s.shiftId = a.shiftId" +
                " WHERE amenityid = ? AND dayid = ?;";
        return jdbcTemplate.query(query, ROW_MAPPER_2, date, amenityId, dayId);
    }

    @Override
    public Optional<Shift> findShiftId(long startTime, long dayId) {
        final List<Shift> list = jdbcTemplate.query(SHIFTS + " WHERE starttime = ? and dayid = ?", ROW_MAPPER, startTime, dayId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Shift> getAmenityShifts(long amenityId) {
        return jdbcTemplate.query(SHIFTS_JOIN_AVAILABILITY_SHIFTS + " WHERE av.amenityavailabilityid = ?", ROW_MAPPER, amenityId);
    }
}