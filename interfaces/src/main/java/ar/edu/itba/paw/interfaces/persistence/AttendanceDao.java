package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Attendance;

public interface AttendanceDao {

    // ---------------------------------------------- EVENTS_USERS INSERT ----------------------------------------------

    Attendance createAttendee(long userId, long eventId);

    // ---------------------------------------------- EVENTS_USERS DELETE ------------------------------------------------

    boolean deleteAttendee(long userId, long eventId);
}
