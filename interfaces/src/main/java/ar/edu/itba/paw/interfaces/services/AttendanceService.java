package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    Attendance createAttendance(String userURN, long eventId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Attendance> findAttendance(long attendanceId, long eventId, long neighborhoodId);

    List<Attendance> getAttendance(long eventId, int page, int size, long neighborhoodId);

    int calculateAttendancePages(long eventId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAttendance(long userId, long eventId);
}
