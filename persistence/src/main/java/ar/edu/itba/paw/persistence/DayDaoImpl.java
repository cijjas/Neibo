package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.models.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class DayDaoImpl implements DayDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(DayDaoImpl.class);
    private static final RowMapper<Day> ROW_MAPPER =
            (rs, rowNum) -> new Day.Builder()
                    .dayId(rs.getLong("dayid"))
                    .dayName(rs.getString("dayname"))
                    .build();
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String DAYS = "SELECT * FROM days ";

    // ------------------------------------------------ DAYS INSERT ----------------------------------------------------

    @Autowired
    public DayDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("days")
                .usingGeneratedKeyColumns("dayid");
    }

    // ------------------------------------------------ DAYS SELECT ----------------------------------------------------

    @Override
    public Day createDay(String day) {
        LOGGER.debug("Inserting Day {}", day);
        Map<String, Object> data = new HashMap<>();
        data.put("dayname", day);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Day.Builder()
                    .dayName(day)
                    .dayId(key.longValue())
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Day", ex);
            throw new InsertionException("An error occurred whilst creating the Day");
        }
    }

    @Override
    public Optional<Day> findDayById(long dayId) {
        LOGGER.debug("Selecting Day with id {}", dayId);
        final List<Day> list = jdbcTemplate.query(DAYS + " WHERE dayid = ?", ROW_MAPPER, dayId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
