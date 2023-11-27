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

@Repository
public class AttendanceDaoImpl implements AttendanceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

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