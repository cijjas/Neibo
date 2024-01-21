package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.Optional;
import java.util.Set;

public interface AttendanceService {

    Attendance createAttendance(long userId, long eventId);

    // -----------------------------------------------------------------------------------------------------------------

    Set<Attendance> getAttendance(long eventId, int page, int size, long neighborhoodId);

    Optional<Attendance> findAttendance(long attendanceId, long eventId, long neighborhoodId);

    Optional<Attendance> findAttendance(long attendanceId);

    // ---------------------------------------------------

    int countAttendance(long eventId);

    int calculateAttendancePages(long eventId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    void deleteAttendance(long userId, long eventId);
}
