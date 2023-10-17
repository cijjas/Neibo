package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.DayTime;
import ar.edu.itba.paw.models.Shift;
import enums.StandardTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.channels.SelectableChannel;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AmenityServiceImpl implements AmenityService {
    private final AmenityDao amenityDao;
    private final ShiftDao shiftDao;
    private final AvailabilityDao availabilityDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityServiceImpl.class);

    @Autowired
    public AmenityServiceImpl(final AmenityDao amenityDao, final ShiftDao shiftDao, final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
        this.shiftDao = shiftDao;
        this.amenityDao = amenityDao;
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, long neighborhoodId, List<String> selectedShifts) {
        LOGGER.info("Creating Amenity {}", name);
        Amenity amenity = amenityDao.createAmenity(name, description, neighborhoodId);

        for (String shiftPair : selectedShifts) {
            // Parse the pair into <Long dayId, Long timeId>
            String[] shiftParts = shiftPair.split(",");

            long dayId = Long.parseLong(shiftParts[0]);
            long timeId = Long.parseLong(shiftParts[1]);

            Optional<Shift> existingShift = shiftDao.findShiftId(timeId, dayId);

            if (existingShift.isPresent()) {
                availabilityDao.createAvailability(amenity.getAmenityId(), existingShift.get().getShiftId());
            } else {
                // Shift doesn't exist, create a new shift and then create an availability entry
                Shift newShift = shiftDao.createShift(dayId, timeId);
                availabilityDao.createAvailability(amenity.getAmenityId(), newShift.getShiftId());
            }
        }

        return amenity;
    }

    @Override
    public Optional<Amenity> findAmenityById(long amenityId) {
        return amenityDao.findAmenityById(amenityId);
    }

    @Override
    public List<Amenity> getAmenities(long neighborhoodId) {
        LOGGER.info("Getting Amenities from Neighborhood {}", neighborhoodId);
        return amenityDao.getAmenities(neighborhoodId);
    }

    @Override
    public boolean deleteAmenity(long amenityId) {
        LOGGER.info("Deleting Amenity {}", amenityId);
        return amenityDao.deleteAmenity(amenityId);
    }

}
