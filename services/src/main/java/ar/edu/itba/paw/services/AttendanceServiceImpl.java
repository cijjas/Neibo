package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.interfaces.services.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceDao attendanceDao;

    @Autowired
    public AttendanceServiceImpl(AttendanceDao attendanceDao) {
        this.attendanceDao = attendanceDao;
    }

    @Override
    public void createAttendee(long userId, long eventId) {
        attendanceDao.createAttendee(userId, eventId);
    }

    @Override
    public void deleteAttendee(long userId, long eventId) {
        attendanceDao.deleteAttendee(userId, eventId);
    }

}
