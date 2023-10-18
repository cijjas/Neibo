package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.interfaces.services.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void createAttendee(long userId, long eventId) {
        LOGGER.info("Adding User {} as Attendee for Event {}", userId, eventId);
        attendanceDao.createAttendee(userId, eventId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void deleteAttendee(long userId, long eventId) {
        LOGGER.info("Removing User {} as Attendee for Event {}", userId, eventId);
        attendanceDao.deleteAttendee(userId, eventId);
    }

}
