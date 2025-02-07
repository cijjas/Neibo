package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.List;

public interface AttendanceDao {

    // ---------------------------------------------- ATTENDANCE INSERT ------------------------------------------------

    Attendance createAttendee(long userId, long eventId);

    // ---------------------------------------------- ATTENDANCE SELECT ------------------------------------------------

    List<Attendance> getAttendance(long neighborhoodId, Long eventId, Long userId, int page, int size);

    int countAttendance(long neighborhoodId, Long eventId, Long userId);

    // ---------------------------------------------- ATTENDANCE DELETE ------------------------------------------------

    boolean deleteAttendee(long eventId, long userId);
}
