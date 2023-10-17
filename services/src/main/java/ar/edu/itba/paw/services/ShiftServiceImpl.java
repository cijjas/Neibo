package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Shift;
import ar.edu.itba.paw.enums.DayOfTheWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Calendar;

@Service
public class ShiftServiceImpl implements ShiftService {
    private final ShiftDao shiftDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftServiceImpl.class);

    @Autowired
    public ShiftServiceImpl(final ShiftDao shiftDao) {
        this.shiftDao = shiftDao;
    }

    @Override
    public Optional<Shift> findShift(long startTime, long dayId) {
        LOGGER.info("Finding Shift with Day Id {} and Start Time {}", dayId, startTime);
        return shiftDao.findShiftId(startTime, dayId);
    }

    @Override
    public List<Shift> getShifts(long amenityId, Date date) {
        LOGGER.info("Getting Shifts for Amenity {} on date", amenityId, date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return shiftDao.getShifts(amenityId, DayOfTheWeek.convertToCustomDayId(dayOfWeek), date);
    }

    @Override
    public Shift createShift(long dayId, long timeId) {
        LOGGER.info("Creating Shift for Day {} on Time {}", dayId, timeId);
        return shiftDao.createShift(dayId, timeId);
    }

    @Override
    public List<Shift> getAmenityShifts(long amenityId) {
        return shiftDao.getAmenityShifts(amenityId);
    }
}
