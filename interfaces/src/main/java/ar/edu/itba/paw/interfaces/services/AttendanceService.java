package ar.edu.itba.paw.interfaces.services;

public interface AttendanceService {

    void createAttendee(long userId, long eventId);

    void deleteAttendee(long userId, long eventId);

}
