package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.Optional;
import java.util.Set;

public interface AttendanceService {

    // -----------------------------------------------------------------------------------------------------------------
    Set<Attendance> getAttendance(long eventId, int page, int size);

    Optional<Attendance> findAttendanceById(long attendanceId);

    int getTotalAttendancePages(long eventId, int size);

    Attendance createAttendee(long userId, long eventId);

    // -----------------------------------------------------------------------------------------------------------------

    void deleteAttendee(long userId, long eventId);



}
