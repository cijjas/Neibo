package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Attendance;

import java.util.List;

public interface AttendanceService {

    Attendance createAttendance(long eventId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Attendance> getAttendance(long neighborhoodId, Long eventId, Long userId, int page, int size);

    int countAttendance(long neighborhoodId, Long eventId, Long userId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAttendance(long eventId, long userId);
}
