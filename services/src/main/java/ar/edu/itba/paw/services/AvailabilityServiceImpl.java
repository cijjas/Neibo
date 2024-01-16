package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.AvailabilityService;
import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.models.Entities.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityServiceImpl.class);
    private final ShiftDao shiftDao;
    private final AvailabilityDao availabilityDao;

    @Autowired
    public AvailabilityServiceImpl(final ShiftDao shiftDao, final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
        this.shiftDao = shiftDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Availability createAvailability(long amenityId, long shiftId) {
        return availabilityDao.createAvailability(amenityId, shiftId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Availability> findAvailability(long id) {

        ValidationUtils.checkAvailabilityId(id);

        return availabilityDao.findAvailability(id);
    }

    @Override
    public Optional<Availability> findAvailability(long amenityId, long availabilityId) {

        ValidationUtils.checkAmenityId(amenityId);
        ValidationUtils.checkAvailabilityId(availabilityId);

        return availabilityDao.findAvailability(amenityId, availabilityId);
    }


    @Override
    public List<Availability> getAvailability(long amenityId) {

        ValidationUtils.checkAmenityId(amenityId);

        return availabilityDao.getAvailability(amenityId);
    }

    @Override
    public List<Availability> getAvailability(long amenityId, String status, String date) {

        ValidationUtils.checkAmenityId(amenityId);
        ValidationUtils.checkShiftStatusString(status);
        ValidationUtils.checkDateString(date);

        return availabilityDao.getAvailability(amenityId, status, date);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean updateAvailability(long amenityId, List<String> newShiftDescriptions) {
        LOGGER.info("Updating the Availability for Amenity");

        // Convert the List of Shift descriptions to a List of shift IDs
        List<Long> newShiftIds = newShiftDescriptions.stream()
                .map(shiftDescription -> {
                    String[] shiftParts = shiftDescription.split("-");
                    long dayId = Long.parseLong(shiftParts[0]);
                    long timeId = Long.parseLong(shiftParts[1]);
                    Optional<Shift> existingShift = shiftDao.findShift(timeId, dayId);
                    if (existingShift.isPresent()) {
                        return existingShift.get().getShiftId();
                    } else {
                        Shift newShift = shiftDao.createShift(dayId, timeId);
                        return newShift.getShiftId();
                    }
                })
                .collect(Collectors.toList());

        // Get the old shift IDs
        List<Long> oldShiftIds = shiftDao.getShifts(amenityId).stream()
                .map(Shift::getShiftId)
                .collect(Collectors.toList());

        // Compare old and new shift IDs to update availability
        for (Long shiftId : newShiftIds) {
            if (!oldShiftIds.contains(shiftId)) {
                // + newShifts and - oldShifts -> createAvailability
                availabilityDao.createAvailability(amenityId, shiftId);
            }
        }

        for (Long shiftId : oldShiftIds) {
            if (!newShiftIds.contains(shiftId)) {
                // - newShifts and + oldShifts -> deleteAvailability
                availabilityDao.deleteAvailability(amenityId, shiftId);
            }
        }

        return true; // Indicate success
    }
}
