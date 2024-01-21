package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.DayOfTheWeek;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Entities.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftServiceImpl.class);
    private final ShiftDao shiftDao;

    @Autowired
    public ShiftServiceImpl(final ShiftDao shiftDao) {
        this.shiftDao = shiftDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Shift createShift(long dayId, long timeId) {
        LOGGER.info("Creating Shift for Day {} on Time {}", dayId, timeId);

        return shiftDao.createShift(dayId, timeId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Shift> findShift(long startTime, long dayId) {
        LOGGER.info("Finding Shift with Day {} and Start Time {}", dayId, startTime);

        ValidationUtils.checkShiftIds(startTime, dayId);

        return shiftDao.findShift(startTime, dayId);
    }

    @Override
    public List<Shift> getShifts() {
        LOGGER.info("Getting Shifts");

        return shiftDao.getShifts();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Shift> findShift(long shiftId) {
        LOGGER.info("Finding Shift {}", shiftId);

        ValidationUtils.checkShiftId(shiftId);

        return shiftDao.findShift(shiftId);
    }

    // deprecated

    /*@Override
    @Transactional(readOnly = true)
    public List<Shift> getShifts(long amenityId, Date date) {
        LOGGER.info("Getting Shifts for Amenity {} on date {}", amenityId, date);

        ValidationUtils.checkAmenityId(amenityId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return shiftDao.getShifts(amenityId, DayOfTheWeek.convertToCustomDayId(dayOfWeek), date);
    }*/

    @Override
    @Transactional(readOnly = true)
    public List<Shift> getShifts(long amenityId) {
        LOGGER.info("Getting Shifts for Amenity {}", amenityId);

        ValidationUtils.checkAmenityId(amenityId);

        return shiftDao.getShifts(amenityId);
    }

    /*public List<Shift> getShifts(long amenityId, long dayId, Date date) {

        ValidationUtils.checkAmenityId(amenityId);

        if (dayId > 0 && date != null) {
            // Both dayId and date are provided
            return shiftDao.getShifts(amenityId, dayId, date);
        } else if (dayId > 0) {
            // Only dayId is provided
            return shiftDao.getShifts(amenityId, dayId, null);
        } else {
            // No specific criteria provided, fetch all shifts
            return shiftDao.getShifts(amenityId);
        }
    }*/
}
