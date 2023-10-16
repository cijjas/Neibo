package ar.edu.itba.paw.interfaces.persistence;

public interface AttendanceDao {

    // ---------------------------------------------- EVENTS_USERS INSERT ----------------------------------------------

    void createAttendee(long userId, long eventId);

    // ---------------------------------------------- EVENTS_USERS DELETE ------------------------------------------------

    boolean deleteAttendee(long userId, long eventId);
}
