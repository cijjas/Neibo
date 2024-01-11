package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.Optional;
import java.util.Set;

public interface AttendanceDao {

    // ---------------------------------------------- ATTENDANCE INSERT ------------------------------------------------

    // ---------------------------------------------- ATTENDANCE SELECT ------------------------------------------------
    Set<Attendance> getAttendance(long eventId, int page, int size);

    int getAttendanceCount(long eventId);

    Optional<Attendance> findAttendanceById(long attendanceId);

    Attendance createAttendee(long userId, long eventId);

    // ---------------------------------------------- ATTENDANCE DELETE ------------------------------------------------

    boolean deleteAttendee(long userId, long eventId);
}
