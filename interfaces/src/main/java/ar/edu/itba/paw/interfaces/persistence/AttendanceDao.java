package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.Optional;
import java.util.Set;

public interface AttendanceDao {

    // ---------------------------------------------- ATTENDANCE INSERT ------------------------------------------------

    Attendance createAttendee(long userId, long eventId);

    // ---------------------------------------------- ATTENDANCE SELECT ------------------------------------------------

    Set<Attendance> getAttendance(long eventId, int page, int size);

    Optional<Attendance> findAttendance(long attendanceId, long eventId);

    Optional<Attendance> findAttendance(long attendanceId);

    // ---------------------------------------------------

    int countAttendance(long eventId);

    // ---------------------------------------------- ATTENDANCE DELETE ------------------------------------------------

    boolean deleteAttendee(long userId, long eventId);
}
