package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class EventDaoImpl implements EventDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- EVENT INSERT -----------------------------------------------------

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

    // ---------------------------------------------- EVENT SELECT -----------------------------------------------------

    @Override
    public Optional<Event> findEvent(long eventId) {
        LOGGER.debug("Selecting Event with id {}", eventId);

        return Optional.ofNullable(em.find(Event.class, eventId));
    }

    @Override
    public Optional<Event> findEvent(long eventId, long neighborhoodId) {
        LOGGER.debug("Selecting Event with eventId {}, neighborhoodId {}", eventId, neighborhoodId);

        TypedQuery<Event> query = em.createQuery(
                "SELECT e FROM Event e WHERE e.eventId = :eventId AND e.neighborhood.id = :neighborhoodId",
                Event.class
        );

        query.setParameter("eventId", eventId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Event> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Event> getEvents(String date, long neighborhoodId) {
        LOGGER.debug("Selecting Events from Date {}", date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String jpql = "SELECT e FROM Event e WHERE e.date = :date AND e.neighborhood.neighborhoodId = :neighborhoodId";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class);
        try {
            query.setParameter("date", dateFormat.parse(date));
        } catch (ParseException e) {
            // this code should never execute as the caller methods check for invalid parameters
            throw new IllegalArgumentException("Invalid value (" + date + ") for the 'date' parameter. Please use a date in YYYY-(M)M-(D)D format.");
        }
        query.setParameter("neighborhoodId", neighborhoodId);
        return query.getResultList();
    }

    @Override
    public List<Event> getEvents(long neighborhoodId) {
        LOGGER.debug("Selecting Events from Neighborhood {}", neighborhoodId);
        String jpql = "SELECT e FROM Event e WHERE e.neighborhood.neighborhoodId = :neighborhoodId";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class);
        query.setParameter("neighborhoodId", neighborhoodId);
        return query.getResultList();
    }


    @Override
    public List<Event> getEvents(long neighborhoodId, Date startDate, Date endDate) {
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
        return false;
    }
}
