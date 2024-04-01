package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.models.Entities.Attendance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceServiceImpl.class);
    private final AttendanceDao attendanceDao;
    private final EventDao eventDao;

    @Autowired
    public AttendanceServiceImpl(AttendanceDao attendanceDao, EventDao eventDao) {
        this.attendanceDao = attendanceDao;
        this.eventDao = eventDao;
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Attendance createAttendance(long userId, long eventId) {
        LOGGER.info("Adding User {} as Attendee for Event {}", userId, eventId);
        ValidationUtils.checkAttendanceId(userId, eventId);

        return attendanceDao.createAttendee(userId, eventId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findAttendance(long userId, long eventId, long neighborhoodId) {
        LOGGER.info("Finding Attendance for User {} and Event {}", userId, eventId);

        ValidationUtils.checkAttendanceId(userId, eventId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return attendanceDao.findAttendance(userId, eventId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findAttendance(long attendanceId) {
        LOGGER.info("Finding Attendance {}", attendanceId);

        ValidationUtils.checkAttendanceId(attendanceId);

        return attendanceDao.findAttendance(attendanceId);
    }

    @Override
    public List<Attendance> getAttendance(long eventId, int page, int size, long neighborhoodId) {
        LOGGER.info("Getting Attendance for Event {}", eventId);

        ValidationUtils.checkEventId(eventId);
        ValidationUtils.checkPageAndSize(page, size);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        eventDao.findEvent(eventId, neighborhoodId).orElseThrow(NotFoundException::new);

        return attendanceDao.getAttendance(eventId, page, size);
    }

    // ---------------------------------------------------

    @Override
    public int countAttendance(long eventId) {
        LOGGER.info("Counting Attendance for Event {}", eventId);

        ValidationUtils.checkEventId(eventId);

        return attendanceDao.countAttendance(eventId);
    }

    @Override
    public int calculateAttendancePages(long eventId, int size) {
        LOGGER.info("Calculating Attendance Pages for Event {}", eventId);

        ValidationUtils.checkEventId(eventId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(attendanceDao.countAttendance(eventId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteAttendance(long userId, long eventId) {
        LOGGER.info("Removing User {} as Attendee for Event {}", userId, eventId);

        ValidationUtils.checkAttendanceId(userId, eventId);

        return attendanceDao.deleteAttendee(userId, eventId);
    }
}
