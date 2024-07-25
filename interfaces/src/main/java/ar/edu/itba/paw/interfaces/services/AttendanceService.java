package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AttendanceService {

    Attendance createAttendance(String userURN, long eventId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Attendance> getAttendance(long eventId, int page, int size, long neighborhoodId);

    Optional<Attendance> findAttendance(long attendanceId, long eventId, long neighborhoodId);

    Optional<Attendance> findAttendance(long attendanceId);

    // ---------------------------------------------------

    int countAttendance(long eventId);

    int calculateAttendancePages(long eventId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAttendance(String userURN, long eventId);
}
