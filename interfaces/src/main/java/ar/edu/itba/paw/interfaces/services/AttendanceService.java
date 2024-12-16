package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    Attendance createAttendance(long userId, long eventId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Attendance> findAttendance(long neighborhoodId, long eventId, long attendanceId);

    List<Attendance> getAttendance(long neighborhoodId, long eventId, int size, int page);

    int calculateAttendancePages(long eventId, int size);

    int countAttendance(long neighborhoodId, long eventId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAttendance(long userId, long eventId);
}
