package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Day;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.Time;
import enums.StandardTime;
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
public class TimeDaoImpl implements TimeDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String TIMES = "SELECT * FROM times ";

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeDaoImpl.class);

    @Autowired
    public TimeDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("times")
                .usingGeneratedKeyColumns("timeid");
    }

    // ----------------------------------------------- TIMES INSERT ----------------------------------------------------

    @Override
    public Time createTime(java.sql.Time timeInterval) {
        LOGGER.info("Inserting Time {}", timeInterval.getTime());
        Map<String, Object> data = new HashMap<>();
        data.put("timeinterval", timeInterval);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Time.Builder()
                    .timeId(key.longValue())
                    .timeInterval(timeInterval)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Time", ex);
            throw new InsertionException("An error occurred whilst creating the Time");
        }
    }

    // ----------------------------------------------- TIMES SELECT ----------------------------------------------------

    private static final RowMapper<Time> ROW_MAPPER =
            (rs, rowNum) -> new Time.Builder()
                    .timeId(rs.getLong("timeid"))
                    .timeInterval(rs.getTime("timeinterval"))
                    .build();

    @Override
    public Optional<Time> findTimeById(long timeId) {
        LOGGER.info("Selecting Time with timeId {}", timeId);
        final List<Time> list = jdbcTemplate.query(TIMES + " WHERE timeid = ?", ROW_MAPPER, timeId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
