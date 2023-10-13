package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Shift;
import enums.DayOfTheWeek;
import enums.StandardTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Calendar;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftDao shiftDao;

    @Autowired
    public ShiftServiceImpl(final ShiftDao shiftDao) {
        this.shiftDao = shiftDao;
    }

    @Override
    public Optional<Shift> findShift(long startTime, long dayId) {
        return shiftDao.findShiftId(startTime, dayId);
    }

    @Override
    public List<Shift> getShifts(long amenityId, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return shiftDao.getShifts(amenityId, DayOfTheWeek.convertToCustomDayId(dayOfWeek), date);
    }




    @Override
    public Shift createShift(long dayId, long timeId) {
        return shiftDao.createShift(dayId, timeId);
    }

    @Override
    public List<Shift> getAmenityShifts(long amenityId) {
        return shiftDao.getAmenityShifts(amenityId);
    }
}
