package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.compositeKeys.AttendanceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
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
        LOGGER.debug("Inserting Attendance with User Id {} and Event Id {}", userId, eventId);

        Attendance attendance = new Attendance(em.find(User.class, userId), em.find(Event.class, eventId));
        em.persist(attendance);
        return attendance;
    }
    // ---------------------------------------------- ATTENDANCE SELECT ------------------------------------------------

    @Override
    public Optional<Attendance> findAttendance(long eventId, long userId) {
        LOGGER.debug("Selecting Attendance with Event Id {} and User Id {}", eventId, userId);

        TypedQuery<Attendance> query = em.createQuery(
                "SELECT a FROM Attendance a WHERE a.id = :attendanceId ",
                Attendance.class
        );

        query.setParameter("attendanceId", new AttendanceKey(userId, eventId));

        List<Attendance> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Attendance> getAttendance(long neighborhoodId, Long eventId, Long userId, int page, int size) {
        LOGGER.info("Getting Attendance with Event Id {}, User Id {}, and NeighborhoodId {}", eventId, userId, neighborhoodId);

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Attendance> idRoot = idQuery.from(Attendance.class);
        Join<Attendance, Event> eventJoin = idRoot.join("event");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(eventJoin.get("neighborhood").get("neighborhoodId"), neighborhoodId));
        if (eventId != null) {
            predicates.add(cb.equal(eventJoin.get("eventId"), eventId));
        }
        if (userId != null) {
            predicates.add(cb.equal(idRoot.get("user").get("userId"), userId));
        }

        idQuery.select(idRoot.get("id"))
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(
                        cb.asc(eventJoin.get("eventId")),
                        cb.asc(idRoot.get("user").get("userId"))
                );

        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);

        List<Long> attendanceIds = idTypedQuery.getResultList();
        if (attendanceIds.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaQuery<Attendance> dataQuery = cb.createQuery(Attendance.class);
        Root<Attendance> dataRoot = dataQuery.from(Attendance.class);
        dataQuery.where(dataRoot.get("id").in(attendanceIds))
                .orderBy(
                        cb.asc(dataRoot.get("event").get("eventId")),
                        cb.asc(dataRoot.get("user").get("userId"))
                );

        TypedQuery<Attendance> dataTypedQuery = em.createQuery(dataQuery);
        return dataTypedQuery.getResultList();
    }

    @Override
    public int countAttendance(long neighborhoodId, Long eventId, Long userId) {
        LOGGER.info("Counting Attendance with Event Id {}, User Id {}, and NeighborhoodId {}", eventId, userId, neighborhoodId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Attendance> root = countQuery.from(Attendance.class);
        Join<Attendance, Event> eventJoin = root.join("event");

        // Dynamic Predicate
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(eventJoin.get("neighborhood").get("neighborhoodId"), neighborhoodId));
        if (eventId != null) {
            predicates.add(cb.equal(eventJoin.get("eventId"), eventId));
        }
        if (userId != null) {
            predicates.add(cb.equal(root.get("user").get("userId"), userId));
        }

        countQuery.select(cb.count(root))
                .where(predicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult().intValue();
    }

    // ---------------------------------------------- ATTENDANCE DELETE ------------------------------------------------

    @Override
    public boolean deleteAttendee(long eventId, long userId) {
        LOGGER.debug("Deleting Attendance with Event Id {} and User Id {}", eventId, userId);

        String sql = "DELETE FROM events_users " +
                "WHERE eventid = :eventId " +
                "  AND userid = :userId";

        int rowsAffected = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("eventId", eventId)
                .executeUpdate();

        return rowsAffected > 0;
    }
}