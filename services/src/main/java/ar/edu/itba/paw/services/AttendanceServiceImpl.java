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
    public Attendance createAttendance(long user, long event) {
        LOGGER.info("Adding User {} as Attendee for Event {}", user, event);

        return attendanceDao.createAttendee(user, event);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findAttendance(long userId, long eventId, long neighborhoodId) {
        LOGGER.info("Finding Attendance for User {} and Event {}", userId, eventId);

        return attendanceDao.findAttendance(userId, eventId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendance(long eventId, int page, int size, long neighborhoodId) {
        LOGGER.info("Getting Attendance for Event {}", eventId);

        return attendanceDao.getAttendance(eventId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateAttendancePages(long eventId, int size) {
        LOGGER.info("Calculating Attendance Pages for Event {}", eventId);

        return PaginationUtils.calculatePages(attendanceDao.countAttendance(eventId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteAttendance(long userId, long eventId) {
        LOGGER.info("Removing User {} as Attendee for Event {}", userId, eventId);

        return attendanceDao.deleteAttendee(userId, eventId);
    }
}
