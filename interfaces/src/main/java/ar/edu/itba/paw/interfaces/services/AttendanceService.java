package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    Attendance createAttendance(long eventId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Attendance> findAttendance(long neighborhoodId, long eventId, long userId);

    List<Attendance> getAttendance(long neighborhoodId, Long eventId, Long userId, int size, int page);

    int calculateAttendancePages(long neighborhoodId, Long eventId, Long userId, int size);

    int countAttendance(long neighborhoodId, Long eventId, Long userId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAttendance(long neighborhoodId, long eventId, long userId);
}
