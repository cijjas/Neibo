package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.interfaces.services.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceDao attendanceDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    @Autowired
    public AttendanceServiceImpl(AttendanceDao attendanceDao) {
        this.attendanceDao = attendanceDao;
    }

    @Override
    public void createAttendee(long userId, long eventId) {
        LOGGER.info("Adding User {} as Attendee for Event {}", userId, eventId);
        attendanceDao.createAttendee(userId, eventId);
    }

    @Override
    public void deleteAttendee(long userId, long eventId) {
        LOGGER.info("Removing User {} as Attendee for Event {}", userId, eventId);
        attendanceDao.deleteAttendee(userId, eventId);
    }

}
