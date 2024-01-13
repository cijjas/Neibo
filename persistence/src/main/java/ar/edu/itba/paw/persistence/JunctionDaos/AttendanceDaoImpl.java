package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.models.Entities.*;
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
import java.util.*;

@Repository
public class AttendanceDaoImpl implements AttendanceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- ATTENDANCE SELECT ------------------------------------------------
    @Override
    public Set<Attendance> getAttendance(long eventId, int page, int size) {
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
        // Create the query
        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        // We implement pagination in the query
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);
        // Results
        List<Long> attendanceIds = idTypedQuery.getResultList();
        // Check if amenityIds is empty, better performance
        if (attendanceIds.isEmpty())
            return Collections.emptySet();
        // Second Query is also focused on Amenities
        CriteriaQuery<Attendance> dataQuery = cb.createQuery(Attendance.class);
        Root<Attendance> dataRoot = dataQuery.from(Attendance.class);
        // Add predicate that enforces existence within the IDs recovered in the first query
        dataQuery.where(dataRoot.get("id").in(attendanceIds));
        // Create!
        TypedQuery<Attendance> dataTypedQuery = em.createQuery(dataQuery);
        // Return Results
        return new HashSet<>(dataTypedQuery.getResultList());
    }

    // ---------------------------------------------------

    @Override
    public int getAttendanceCount(long eventId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Attendance> root = criteriaQuery.from(Attendance.class);
        Join<Attendance, Neighborhood> eventJoin = root.join("event"); // Use the association
        criteriaQuery.select(cb.count(root));
        criteriaQuery.where(cb.equal(eventJoin.get("eventId"), eventId)); // Filter by eventId
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult().intValue();
    }

    @Override
    public Optional<Attendance> findAttendanceById(long attendanceId) {
        LOGGER.debug("Selecting Attendance with id {}", attendanceId);
        return Optional.ofNullable(em.find(Attendance.class, attendanceId));
    }

    // ---------------------------------------------- ATTENDANCE INSERT ------------------------------------------------

    @Override
    public Attendance createAttendee(long userId, long eventId) {
        LOGGER.debug("Inserting Attendance");
        Attendance attendance = new Attendance(em.find(User.class, userId), em.find(Event.class, eventId));
        em.persist(attendance);
        return attendance;
    }

    // ---------------------------------------------- ATTENDANCE DELETE ------------------------------------------------

    @Override
    public boolean deleteAttendee(long userId, long eventId) {
        LOGGER.debug("Deleting Attendance with userId {} and eventId {}", userId, eventId);
        Attendance attendance = em.find(Attendance.class, new AttendanceKey(userId, eventId));
        if (attendance != null) {
            em.remove(attendance);
            return true;
        } else {
            return false;
        }
    }
}