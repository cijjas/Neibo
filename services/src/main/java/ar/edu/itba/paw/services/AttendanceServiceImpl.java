package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.interfaces.services.AttendanceService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Attendance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

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
    public Attendance createAttendee(long userId, long eventId) {
        LOGGER.info("Adding User {} as Attendee for Event {}", userId, eventId);
        return attendanceDao.createAttendee(userId, eventId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findAttendanceById(long attendanceId) {

        ValidationUtils.checkId(attendanceId, "Attendance");

        return attendanceDao.findAttendanceById(attendanceId);
    }

    @Override
    public Set<Attendance> getAttendance(long eventId, int page, int size) {
        LOGGER.info("Getting Attendance for Event {}", eventId);

        ValidationUtils.checkId(eventId, "Event");
        ValidationUtils.checkPageAndSize(page, size);

        return attendanceDao.getAttendance(eventId, page, size);
    }

    @Override
    public int getTotalAttendancePages(long eventId, int size) {

        ValidationUtils.checkSize(size);

        return (int) Math.ceil((double) attendanceDao.getAttendanceCount(eventId) / size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void deleteAttendee(long userId, long eventId) {
        LOGGER.info("Removing User {} as Attendee for Event {}", userId, eventId);

        ValidationUtils.checkIds(userId, eventId, "Attendance");

        attendanceDao.deleteAttendee(userId, eventId);
    }
}
