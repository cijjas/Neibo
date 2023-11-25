package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Attendance;

public interface AttendanceDao {

    // ---------------------------------------------- ATTENDANCE INSERT ------------------------------------------------

    Attendance createAttendee(long userId, long eventId);

    // ---------------------------------------------- ATTENDANCE DELETE ------------------------------------------------

    boolean deleteAttendee(long userId, long eventId);
}
