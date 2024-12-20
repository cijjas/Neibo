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
import java.util.Collections;
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
    public Event createEvent(long neighborhoodId, String name, String description, Date date, long startTimeId, long endTimeId) {
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
    public Optional<Event> findEvent(long neighborhoodId, long eventId) {
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
    public List<Event> getEvents(long neighborhoodId, Date date, int page, int size) {
        LOGGER.debug("Selecting Events from Date {}", date);

        // Build the JPQL query for fetching event IDs
        StringBuilder jpqlBuilder = new StringBuilder("SELECT e.eventId FROM Event e WHERE e.neighborhood.neighborhoodId = :neighborhoodId");

        if (date != null) {
            jpqlBuilder.append(" AND e.date = :date");
        }

        // Append the ORDER BY clause
        jpqlBuilder.append(" ORDER BY e.date, e.startTime, e.eventId");

        // Create the query
        TypedQuery<Long> query = em.createQuery(jpqlBuilder.toString(), Long.class);

        // Set the parameters
        if (date != null) {
            query.setParameter("date", date);
        }

        query.setParameter("neighborhoodId", neighborhoodId);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        // Get the list of event IDs
        List<Long> eventIds = query.getResultList();

        if (!eventIds.isEmpty()) {
            // Build the JPQL query for fetching events
            TypedQuery<Event> eventQuery = em.createQuery(
                    "SELECT e FROM Event e WHERE e.eventId IN :eventIds ORDER BY e.date, e.startTime, e.eventId", Event.class);
            eventQuery.setParameter("eventIds", eventIds);
            return eventQuery.getResultList();
        }

        return Collections.emptyList();
    }


    @Override
    public int countEvents(long neighborhoodId, Date date) {
        LOGGER.debug("Counting Events with neighborhoodId {}", neighborhoodId);

        StringBuilder jpqlBuilder = new StringBuilder("SELECT DISTINCT COUNT(e.eventId) FROM Event e WHERE e.neighborhood.neighborhoodId = :neighborhoodId");

        if (date != null) {
            jpqlBuilder.append(" AND e.date = :date");
        }

        TypedQuery<Long> query = em.createQuery(jpqlBuilder.toString(), Long.class);

        if (date != null) {
            query.setParameter("date", date);
        }

        query.setParameter("neighborhoodId", neighborhoodId);

        return query.getSingleResult().intValue();
    }

    // ---------------------------------------------- EVENT DELETE -----------------------------------------------------

    @Override
    public boolean deleteEvent(long neighborhoodId, long eventId) {
        LOGGER.debug("Deleting Event with eventId {} and neighborhoodId {}", eventId, neighborhoodId);

        String hql = "DELETE FROM Event e WHERE e.eventId = :eventId " +
                "AND e.neighborhood.id = :neighborhoodId";

        int deletedCount = em.createQuery(hql)
                .setParameter("eventId", eventId)
                .setParameter("neighborhoodId", neighborhoodId)
                .executeUpdate();

        return deletedCount > 0;
    }
}
