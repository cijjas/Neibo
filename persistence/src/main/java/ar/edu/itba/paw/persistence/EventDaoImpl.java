package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.models.Event;

import java.sql.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class EventDaoImpl implements EventDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jdbcInsertTimes;

    private final String EVENTS ="SELECT e.* FROM events e";

    private final String EVENTS_JOIN_NEIGHBORHOODS =
            "SELECT e.*\n" +
            "FROM events e JOIN neighborhoods nh ON e.neighborhoodid = nh.neighborhoodid ";

    private static final Logger LOGGER = LoggerFactory.getLogger(EventDaoImpl.class);

    @Autowired
    public EventDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("eventid")
                .withTableName("events");
        this.jdbcInsertTimes = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("timeid")
                .withTableName("times");
    }

    // ---------------------------------------------- EVENTS INSERT ----------------------------------------------------

    @Override
    public Event createEvent(final String name, final String description, final Date date, final Time startTime, final Time endTime, final long neighborhoodId) {
        LOGGER.debug("Inserting Event {}", name);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("date", date);
        data.put("duration", (endTime.getTime() - startTime.getTime())/60000);
        data.put("neighborhoodid", neighborhoodId);

        // Check if the same time interval entry already exists in the 'times' table
        List<Long> existingTimeIds = jdbcTemplate.queryForList("select timeid from times where timeinterval = ?", Long.class, startTime);

        long startTimeId;

        if(existingTimeIds.isEmpty()) {
            Map<String, Object> timesData = new HashMap<>();
            timesData.put("timeinterval", startTime);

            startTimeId = jdbcInsertTimes.executeAndReturnKey(timesData).longValue();
        } else {
            startTimeId = existingTimeIds.get(0);
        }

        existingTimeIds = jdbcTemplate.queryForList("select timeid from times where timeinterval = ?", Long.class, endTime);

        long endTimeId;

        if(existingTimeIds.isEmpty()) {
            Map<String, Object> timesData = new HashMap<>();
            timesData.put("timeinterval", endTime);

            endTimeId = jdbcInsertTimes.executeAndReturnKey(timesData).longValue();
        } else {
            endTimeId = existingTimeIds.get(0);
        }

        data.put("starttimeid", startTimeId);
        data.put("endtimeid", endTimeId);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Event.Builder()
                    .eventId(key.longValue())
                    .name(name)
                    .description(description)
                    .date(date)
                    .duration(endTime.getTime() - startTime.getTime())
                    .neighborhoodId(neighborhoodId)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Event", ex);
            throw new InsertionException("An error occurred whilst creating the event");
        }
    }

    // ---------------------------------------------- EVENTS SELECT ----------------------------------------------------

    private static final RowMapper<Event> ROW_MAPPER = (rs, rowNum) -> new Event.Builder()
            .eventId(rs.getLong("eventid"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .date(rs.getDate("date"))
            .duration(rs.getLong("duration"))
            .neighborhoodId(rs.getLong("neighborhoodid"))
            .build();

    @Override
    public Optional<Event> findEventById(long eventId) {
        LOGGER.debug("Selecting Event with id {}", eventId);
        final List<Event> list = jdbcTemplate.query(EVENTS + " WHERE eventid = ?", ROW_MAPPER, eventId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<Long> findStartTimeIdByEventId(long eventId) {
        final List<Long> list = jdbcTemplate.queryForList("SELECT starttimeid FROM events WHERE eventid = ?", Long.class, eventId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<Long> findEndTimeIdByEventId(long eventId) {
        final List<Long> list = jdbcTemplate.queryForList("SELECT endtimeid FROM events WHERE eventid = ?", Long.class, eventId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Event> getEventsByDate(Date date, long neighborhoodId) {
        LOGGER.debug("Selecting Events from Date {}", date);
        return jdbcTemplate.query(EVENTS + " WHERE date = ? AND neighborhoodid = ?", ROW_MAPPER, date, neighborhoodId);
    }

    @Override
    public List<Event> getEventsByNeighborhoodId(long neighborhoodId) {
        LOGGER.debug("Selecting Events from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.query(EVENTS + " WHERE neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }

    @Override
    public List<Date> getEventDates(long neighborhoodId) {
        LOGGER.debug("Selecting Event Dates from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.queryForList("SELECT DISTINCT date FROM events WHERE neighborhoodid = ?", Date.class, neighborhoodId);
    }

    // ---------------------------------------------- EVENTS DELETE ----------------------------------------------------

    @Override
    public boolean deleteEvent(long eventId){
        LOGGER.debug("Deleting Event with id {}", eventId);
        return jdbcTemplate.update("DELETE FROM events WHERE eventid = ?", eventId) > 0;
    }
}
