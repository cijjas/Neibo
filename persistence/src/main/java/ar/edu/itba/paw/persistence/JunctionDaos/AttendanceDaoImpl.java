package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.compositeKeys.AttendanceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class AttendanceDaoImpl implements AttendanceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- ATTENDANCE INSERT ------------------------------------------------

    @Override
    public Attendance createAttendee(long userId, long eventId) {
        LOGGER.debug("Inserting Attendance with userId {} and eventId {}", userId, eventId);

        Attendance attendance = new Attendance(em.find(User.class, userId), em.find(Event.class, eventId));
        em.persist(attendance);
        return attendance;
    }
    // ---------------------------------------------- ATTENDANCE SELECT ------------------------------------------------

    @Override
    public Optional<Attendance> findAttendance(long neighborhoodId, long userId, long eventId) {
        LOGGER.debug("Selecting Attendance with id {} and neighborhoodId {}", userId, neighborhoodId);

        TypedQuery<Attendance> query = em.createQuery(
                "SELECT a FROM Attendance a WHERE a.id = :attendanceId " + " AND a.event.neighborhood.neighborhoodId = :neighborhoodId",
                Attendance.class
        );

        query.setParameter("attendanceId", new AttendanceKey(userId, eventId));
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Attendance> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Attendance> getAttendance(long eventId, int page, int size) {
        LOGGER.info("Getting Attendance for Event {}", eventId);

        // Initialize Query Builder
        CriteriaBuilder cb = em.getCriteriaBuilder();
        // We retrieve a list of attendance ids which are Longs, from the Attendance Entity
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Attendance> idRoot = idQuery.from(Attendance.class);
        idQuery.select(idRoot.get("id"));
        // We join through the eventId
        Join<Attendance, Event> eventJoin = idRoot.join("event");
        idQuery.where(cb.equal(eventJoin.get("eventId"), eventId));
        // Order by EventId and the UserId
        idQuery.orderBy(cb.asc(idRoot.get("event").get("eventId")), cb.asc(idRoot.get("user").get("userId")));
        // Create the query
        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        // We implement pagination in the query
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);
        // Results
        List<Long> attendanceIds = idTypedQuery.getResultList();
        // Check if amenityIds is empty, better performance
        if (attendanceIds.isEmpty())
            return Collections.emptyList();
        // Second Query is also focused on Amenities
        CriteriaQuery<Attendance> dataQuery = cb.createQuery(Attendance.class);
        Root<Attendance> dataRoot = dataQuery.from(Attendance.class);
        // Add predicate that enforces existence within the IDs recovered in the first query
        dataQuery.where(dataRoot.get("id").in(attendanceIds));
        // Order By EventId and then by UserId
        dataQuery.orderBy(cb.asc(dataRoot.get("event").get("eventId")), cb.asc(dataRoot.get("user").get("userId")));
        // Create!
        TypedQuery<Attendance> dataTypedQuery = em.createQuery(dataQuery);
        // Return Results
        return dataTypedQuery.getResultList();
    }

    @Override
    public int countAttendance(long eventId) {
        LOGGER.info("Counting Attendance for Event {}", eventId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Attendance> root = criteriaQuery.from(Attendance.class);
        Join<Attendance, Neighborhood> eventJoin = root.join("event"); // Use the association
        criteriaQuery.select(cb.count(root));
        criteriaQuery.where(cb.equal(eventJoin.get("eventId"), eventId)); // Filter by eventId
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult().intValue();
    }


    // ---------------------------------------------- ATTENDANCE DELETE ------------------------------------------------

    @Override
    public boolean deleteAttendee(long neighborhoodId, long eventId, long userId) {
        LOGGER.debug("Deleting Attendance with userId {}, eventId {}, and neighborhoodId {}", userId, eventId, neighborhoodId);

        String sql = "DELETE FROM events_users " +
                "WHERE eventid = :eventId " +
                "  AND userid = :userId " +
                "  AND eventid IN ( " +
                "      SELECT e.eventid " +
                "      FROM events e " +
                "      WHERE e.eventid = :eventId " +
                "        AND e.neighborhoodid = :neighborhoodId " +
                "  )";

        int rowsAffected = em.createNativeQuery(sql)
                .setParameter("neighborhoodId", neighborhoodId)
                .setParameter("userId", userId)
                .setParameter("eventId", eventId)
                .executeUpdate();

        return rowsAffected > 0;
    }
}