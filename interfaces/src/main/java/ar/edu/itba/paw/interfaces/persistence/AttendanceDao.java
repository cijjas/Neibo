package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceDao {

    // ---------------------------------------------- ATTENDANCE INSERT ------------------------------------------------

    Attendance createAttendee(long userId, long eventId);

    // ---------------------------------------------- ATTENDANCE SELECT ------------------------------------------------

    Optional<Attendance> findAttendance(long neighborhoodId, long eventId, long userId);

    List<Attendance> getAttendance(long neighborhoodId, Long eventId, Long userId, int page, int size);

    int countAttendance(long neighborhoodId, Long eventId, Long userId);

    // ---------------------------------------------- ATTENDANCE DELETE ------------------------------------------------

    boolean deleteAttendee(long neighborhoodId, long eventId, long userId);
}
