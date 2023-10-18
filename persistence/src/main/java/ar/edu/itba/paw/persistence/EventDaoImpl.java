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
import java.util.*;

@Repository
public class EventDaoImpl implements EventDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String EVENTS = "SELECT e.* FROM events e";
    private final String EVENTS_JOIN_TIMES =
            "SELECT e.*, t1.timeinterval AS starttime, t2.timeinterval AS endtime\n" +
            "FROM events e\n" +
                    "INNER JOIN times t1 ON e.starttimeid = t1.timeid\n" +
                    "INNER JOIN times t2 ON e.endtimeid = t2.timeid ";

    private static final Logger LOGGER = LoggerFactory.getLogger(EventDaoImpl.class);

    @Autowired
    public EventDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("eventid")
                .withTableName("events");
    }

    // ---------------------------------------------- EVENT INSERT -----------------------------------------------------

    @Override
    public Event createEvent(final String name, final String description, final Date date, final long startTimeId, final long endTimeId, final long neighborhoodId) {
        LOGGER.debug("Inserting Event {}", name);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("date", date);
        data.put("neighborhoodid", neighborhoodId);
        data.put("starttimeid", startTimeId);
        data.put("endtimeid", endTimeId);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Event.Builder()
                    .eventId(key.longValue())
                    .name(name)
                    .description(description)
                    .date(date)
                    .neighborhoodId(neighborhoodId)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Event", ex);
            throw new InsertionException("An error occurred whilst creating the event");
        }
    }

    // ---------------------------------------------- EVENT SELECT -----------------------------------------------------

    private static final RowMapper<Event> ROW_MAPPER = (rs, rowNum) -> new Event.Builder()
            .eventId(rs.getLong("eventid"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .date(rs.getDate("date"))
            .startTime(rs.getTime("starttime"))
            .endTime(rs.getTime("endtime"))
            .neighborhoodId(rs.getLong("neighborhoodid"))
            .build();

    @Override
    public Optional<Event> findEventById(long eventId) {
        LOGGER.debug("Selecting Event with id {}", eventId);
        final List<Event> list = jdbcTemplate.query(EVENTS_JOIN_TIMES + " where eventid = ?", ROW_MAPPER, eventId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Event> getEventsByDate(Date date, long neighborhoodId) {
        LOGGER.debug("Selecting Events from Date {}", date);
        return jdbcTemplate.query(EVENTS_JOIN_TIMES + " where date = ? and neighborhoodid = ?", ROW_MAPPER, date, neighborhoodId);
    }

    @Override
    public List<Event> getEventsByNeighborhoodId(long neighborhoodId) {
        LOGGER.debug("Selecting Events from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.query(EVENTS_JOIN_TIMES + " where neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }

    @Override
    public List<Date> getEventDates(long neighborhoodId) {
        LOGGER.debug("Selecting Event Dates from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.queryForList("select distinct date from events where neighborhoodid = ?", Date.class, neighborhoodId);
    }

    // ---------------------------------------------- EVENT DELETE -----------------------------------------------------

    @Override
    public boolean deleteEvent(long eventId){
        LOGGER.debug("Deleting Event with id {}", eventId);
        return jdbcTemplate.update("DELETE FROM events WHERE eventid = ?", eventId) > 0;
    }
}
