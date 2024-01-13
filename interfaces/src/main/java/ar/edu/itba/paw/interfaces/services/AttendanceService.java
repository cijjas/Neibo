package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.Optional;
import java.util.Set;

public interface AttendanceService {

    Attendance createAttendee(long userId, long eventId);

    // -----------------------------------------------------------------------------------------------------------------
    Set<Attendance> getAttendance(long eventId, int page, int size);

    Optional<Attendance> findAttendanceById(long attendanceId);

    // ---------------------------------------------------

    int countAttendees(long eventId);

    int calculateAttendancePages(long eventId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    void deleteAttendee(long userId, long eventId);



}
