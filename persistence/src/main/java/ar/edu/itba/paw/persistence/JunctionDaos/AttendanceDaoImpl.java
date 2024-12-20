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
    public List<Attendance> getAttendance(long neighborhoodId, long eventId, int page, int size) {
        LOGGER.info("Getting Attendance for Event {} in Neighborhood {}", eventId, neighborhoodId);

        // Initialize Query Builder
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // First Query: Retrieve attendance IDs
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Attendance> idRoot = idQuery.from(Attendance.class);

        // Join through the event and add filters for eventId and neighborhoodId
        Join<Attendance, Event> eventJoin = idRoot.join("event");
        idQuery.select(idRoot.get("id"))
                .where(cb.and(
                        cb.equal(eventJoin.get("eventId"), eventId),
                        cb.equal(eventJoin.get("neighborhood").get("neighborhoodId"), neighborhoodId)
                ))
                .orderBy(
                        cb.asc(eventJoin.get("eventId")),
                        cb.asc(idRoot.get("user").get("userId"))
                );

        // Implement pagination
        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);

        // Retrieve attendance IDs
        List<Long> attendanceIds = idTypedQuery.getResultList();
        if (attendanceIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Second Query: Retrieve Attendance data
        CriteriaQuery<Attendance> dataQuery = cb.createQuery(Attendance.class);
        Root<Attendance> dataRoot = dataQuery.from(Attendance.class);

        // Add filter to ensure IDs exist in the retrieved list
        dataQuery.where(dataRoot.get("id").in(attendanceIds))
                .orderBy(
                        cb.asc(dataRoot.get("event").get("eventId")),
                        cb.asc(dataRoot.get("user").get("userId"))
                );

        TypedQuery<Attendance> dataTypedQuery = em.createQuery(dataQuery);
        return dataTypedQuery.getResultList();
    }

    @Override
    public int countAttendance(long neighborhoodId, long eventId) {
        LOGGER.info("Counting Attendance for Event {} in Neighborhood {}", eventId, neighborhoodId);

        // Initialize Query Builder
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Attendance> root = criteriaQuery.from(Attendance.class);

        // Join through the event and add filters for eventId and neighborhoodId
        Join<Attendance, Event> eventJoin = root.join("event");
        criteriaQuery.select(cb.count(root))
                .where(cb.and(
                        cb.equal(eventJoin.get("eventId"), eventId),
                        cb.equal(eventJoin.get("neighborhood").get("neighborhoodId"), neighborhoodId)
                ));

        // Execute the query and return count
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