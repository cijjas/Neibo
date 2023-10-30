package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.models.MainEntities.Contact;
import ar.edu.itba.paw.models.MainEntities.Event;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;
import ar.edu.itba.paw.models.MainEntities.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.util.*;

@Repository
public class EventDaoImpl implements EventDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private static final RowMapper<Event> ROW_MAPPER = (rs, rowNum) -> new Event.Builder()
            .eventId(rs.getLong("eventid"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .date(rs.getDate("date"))
            /*.startTime(rs.getTime("starttime"))
            .endTime(rs.getTime("endtime"))
            .neighborhoodId(rs.getLong("neighborhoodid"))*/
            .build();
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String EVENTS = "SELECT e.* FROM events e";
    private final String EVENTS_JOIN_TIMES =
            "SELECT e.*, t1.timeinterval AS starttime, t2.timeinterval AS endtime\n" +
                    "FROM events e\n" +
                    "INNER JOIN times t1 ON e.starttimeid = t1.timeid\n" +
                    "INNER JOIN times t2 ON e.endtimeid = t2.timeid ";

    // ---------------------------------------------- EVENT INSERT -----------------------------------------------------

    @Autowired
    public EventDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("eventid")
                .withTableName("events");
    }

    // ---------------------------------------------- EVENT SELECT -----------------------------------------------------

    @Override
    public Event createEvent(final String name, final String description, final Date date, final long startTimeId, final long endTimeId, final long neighborhoodId) {
        LOGGER.debug("Inserting Event {}", name);
        Event event = new Event.Builder()
                .name(name)
                .description(description)
                .date(date)
                .startTime(em.find(Time.class, startTimeId))
                .endTime(em.find(Time.class, endTimeId))
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .build();
        em.persist(event);
        return event;
    }

    @Override
    public Optional<Event> findEventById(long eventId) {
        LOGGER.debug("Selecting Event with id {}", eventId);
        return Optional.ofNullable(em.find(Event.class, eventId));
    }

    @Override
    public List<Event> getEventsByDate(Date date, long neighborhoodId) {
        LOGGER.debug("Selecting Events from Date {}", date);
        String jpql = "SELECT e FROM Event e WHERE e.date = :date AND e.neighborhood.neighborhoodId = :neighborhoodId";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class);
        query.setParameter("date", date);
        query.setParameter("neighborhoodId", neighborhoodId);
        return query.getResultList();
    }


    @Override
    public List<Event> getEventsByNeighborhoodId(long neighborhoodId) {
        LOGGER.debug("Selecting Events from Neighborhood {}", neighborhoodId);
        String jpql = "SELECT e FROM Event e WHERE e.neighborhood.neighborhoodId = :neighborhoodId";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class);
        query.setParameter("neighborhoodId", neighborhoodId);
        return query.getResultList();
    }


    @Override
    public List<Event> getEventsByNeighborhoodIdAndDateRange(long neighborhoodId, Date startDate, Date endDate) {
        LOGGER.debug("Selecting Events from Neighborhood {} between {} and {}", neighborhoodId, startDate, endDate);
        String jpql = "SELECT e FROM Event e WHERE e.neighborhood.neighborhoodId = :neighborhoodId AND e.date BETWEEN :startDate AND :endDate";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class);
        query.setParameter("neighborhoodId", neighborhoodId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }


    @Override
    public List<Date> getEventDates(long neighborhoodId) {
        LOGGER.debug("Selecting Event Dates from Neighborhood {}", neighborhoodId);
        String jpql = "SELECT DISTINCT e.date FROM Event e WHERE e.neighborhood.neighborhoodId = :neighborhoodId";
        TypedQuery<Date> query = em.createQuery(jpql, Date.class);
        query.setParameter("neighborhoodId", neighborhoodId);
        return query.getResultList();
    }


    @Override
    public boolean isUserSubscribedToEvent(long userId, long eventId) {
        LOGGER.debug("Selecting if User {} is subscribed to Event {}", userId, eventId);
        String jpql = "SELECT COUNT(u) FROM User u JOIN u.eventsSubscribed e WHERE u.userId = :userId AND e.eventId = :eventId";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("userId", userId);
        query.setParameter("eventId", eventId);
        Long count = query.getSingleResult();
        return count != null && count > 0;
    }


    // ---------------------------------------------- EVENT DELETE -----------------------------------------------------

    @Override
    public boolean deleteEvent(long eventId) {
        LOGGER.debug("Deleting Event with id {}", eventId);
        Event event = em.find(Event.class, eventId);
        if (event != null) {
            em.remove(event);
            return true;
        }
        return false;    }
}
