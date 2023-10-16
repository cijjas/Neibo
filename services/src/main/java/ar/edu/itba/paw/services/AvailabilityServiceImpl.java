package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.AvailabilityService;
import ar.edu.itba.paw.models.Shift;
import enums.StandardTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {
    private final ShiftDao shiftDao;
    private final AvailabilityDao availabilityDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityServiceImpl.class);

    @Autowired
    public AvailabilityServiceImpl(final ShiftDao shiftDao, final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
        this.shiftDao = shiftDao;
    }
    @Override
    public boolean updateAvailability(long amenityId, List<Long> newShifts) {
        LOGGER.info("Updating the Availability for Amenity", amenityId);
        // Convert the List of Shift objects to a List of long shift IDs
        List<Long> oldShiftIds = shiftDao.getAmenityShifts(amenityId).stream()
                .map(Shift::getShiftId)
                .collect(Collectors.toList());

        // All possible shift times are represented by the StandardTime enum
        for (StandardTime standardTime : StandardTime.values()) {
            long shiftId = standardTime.getId();
            boolean inOldShifts = oldShiftIds.contains(shiftId);
            boolean inNewShifts = newShifts.contains(shiftId);
            if (inNewShifts && !inOldShifts) {
                // + newShifts and - oldShifts -> createAvailability
                availabilityDao.createAvailability(amenityId, shiftId);
            } else if (!inNewShifts && inOldShifts) {
                // - newShifts and + oldShifts -> deleteAvailability
                availabilityDao.deleteAvailability(amenityId, shiftId);
            }
            // For other cases, do nothing
        }
        return true; // Indicate success
    }


}
