package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.models.compositeKeys.AttendanceKey;
import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.models.JunctionEntities.Attendance;
import ar.edu.itba.paw.models.MainEntities.Event;
import ar.edu.itba.paw.models.MainEntities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AttendanceDaoImpl implements AttendanceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- EVENTS_USERS INSERT ----------------------------------------------

    @Override
    public Attendance createAttendee(long userId, long eventId) {
        LOGGER.debug("Inserting Attendance");
        Attendance attendance = new Attendance(em.find(User.class, userId), em.find(Event.class, eventId));
        em.persist(attendance);
        return attendance;
    }

    // ---------------------------------------------- EVENTS_USERS DELETE ------------------------------------------------

    @Override
    public boolean deleteAttendee(long userId, long eventId) {
        LOGGER.debug("Deleting Attendance with userId {} and eventId {}", userId, eventId);
        Attendance attendance = em.find(Attendance.class, new AttendanceKey(userId, eventId));
        if (attendance != null) {
            em.remove(attendance); // Delete the entity
            return true;
        } else {
            return false;
        }
    }
}