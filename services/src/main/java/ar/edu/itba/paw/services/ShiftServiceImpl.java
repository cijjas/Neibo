package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Entities.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    @Transactional(readOnly = true)
    public List<Shift> getShifts(String amenityURN, Date date) {
        LOGGER.info("Getting Shifts");

        Long amenityId = ValidationUtils.checkURNAndExtractAmenityId(amenityURN);

        return shiftDao.getShifts(amenityId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Shift> findShift(long shiftId) {
        LOGGER.info("Finding Shift {}", shiftId);

        ValidationUtils.checkShiftId(shiftId);

        return shiftDao.findShift(shiftId);
    }
}
