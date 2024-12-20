package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.models.Entities.Attendance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    private final AttendanceDao attendanceDao;

    @Autowired
    public AttendanceServiceImpl(AttendanceDao attendanceDao) {
        this.attendanceDao = attendanceDao;
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Attendance createAttendance(long event, long user) {
        LOGGER.info("Adding User {} as Attendee for Event {}", user, event);

        return attendanceDao.createAttendee(user, event);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findAttendance(long neighborhoodId, long eventId, long userId) {
        LOGGER.info("Finding Attendance for User {} and Event {}", userId, eventId);

        return attendanceDao.findAttendance(neighborhoodId, userId, eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendance(long neighborhoodId, long eventId, int size, int page) {
        LOGGER.info("Getting Attendance for Event {}", eventId);

        return attendanceDao.getAttendance(neighborhoodId, eventId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateAttendancePages(long neighborhoodId, long eventId, int size) {
        LOGGER.info("Calculating Attendance Pages for Event {}", eventId);

        return PaginationUtils.calculatePages(attendanceDao.countAttendance(neighborhoodId, eventId), size);
    }

    @Override
    public int countAttendance(long neighborhoodId, long eventId) {
        LOGGER.info("Counting Attendees for Event {}", eventId);

        return attendanceDao.countAttendance(neighborhoodId, eventId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteAttendance(long neighborhoodId, long eventId, long userId) {
        LOGGER.info("Removing User {} as Attendee for Event {} in Neighborhood {}", userId, eventId, neighborhoodId);

        return attendanceDao.deleteAttendee(neighborhoodId, eventId, userId);
    }
}
