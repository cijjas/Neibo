package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.models.Event;

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
    private final String EVENTS =
            "select e.* \n" +
                    "from events e";
    private final String EVENTS_JOIN_NEIGHBORHOODS =
            "select e.*\n" +
                    "from events e join neighborhoods nh on e.neighborhoodid = nh.neighborhoodid ";

    private static final Logger LOGGER = LoggerFactory.getLogger(EventDaoImpl.class);

    @Autowired
    public EventDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("eventid")
                .withTableName("events");
    }

    @Override
    public Event createEvent(final String name, final String description, final Date date, final long duration, final long neighborhoodId) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("date", date);
        data.put("duration", duration);
        data.put("neighborhoodid", neighborhoodId);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Event.Builder()
                    .eventId(key.longValue())
                    .name(name)
                    .description(description)
                    .date(date)
                    .duration(duration)
                    .neighborhoodId(neighborhoodId)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Event", ex);
            throw new InsertionException("An error occurred whilst creating the event");
        }
    }

    private static final RowMapper<Event> ROW_MAPPER = (rs, rowNum) -> new Event.Builder()
            .eventId(rs.getLong("eventid"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .date(rs.getDate("date"))
            .duration(rs.getLong("duration"))
            .neighborhoodId(rs.getLong("neighborhoodid"))
            .build();

    @Override
    public List<Event> getEvents() {
        return jdbcTemplate.query(EVENTS, ROW_MAPPER);
    }

    @Override
    public Optional<Event> findEventById(long eventId) {
        final List<Event> list = jdbcTemplate.query(EVENTS + " where eventid = ?", ROW_MAPPER, eventId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Event> getEventsByDate(Date date, long neighborhoodId) {
        return jdbcTemplate.query(EVENTS + " where date = ? and neighborhoodid = ?", ROW_MAPPER, date, neighborhoodId);
    }

    @Override
    public List<Event> getEventsByNeighborhoodId(long neighborhoodId) {
        return jdbcTemplate.query(EVENTS + " where neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }

    @Override
    public List<Date> getEventDates(long neighborhoodId) {
        return jdbcTemplate.queryForList("select distinct date from events where neighborhoodid = ?", Date.class, neighborhoodId);
    }

    @Override
    public boolean deleteEvent(long eventId){
        return jdbcTemplate.update("DELETE FROM events WHERE eventid = ?", eventId) > 0;
    }
}
