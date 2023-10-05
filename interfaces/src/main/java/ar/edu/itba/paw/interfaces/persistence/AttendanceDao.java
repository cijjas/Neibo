package ar.edu.itba.paw.interfaces.persistence;

public interface AttendanceDao {
    void createAttendee(long userId, long eventId);

    void deleteAttendee(long userId, long eventId);
}
