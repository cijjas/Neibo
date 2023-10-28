package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.MainEntities.Amenity;
import ar.edu.itba.paw.models.MainEntities.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AmenityServiceImpl implements AmenityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityServiceImpl.class);
    private final AmenityDao amenityDao;
    private final ShiftDao shiftDao;
    private final AvailabilityDao availabilityDao;

    @Autowired
    public AmenityServiceImpl(final AmenityDao amenityDao, final ShiftDao shiftDao, final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
        this.shiftDao = shiftDao;
        this.amenityDao = amenityDao;
    }


    // -----------------------------------------------------------------------------------------------------------------

    // Function has to create the shifts if they do not already exist and match them with the amenity through the junction table
    @Override
    public Amenity createAmenity(String name, String description, long neighborhoodId, List<String> selectedShifts) {
        LOGGER.info("Creating Amenity {}", name);
        Amenity amenity = amenityDao.createAmenity(name, description, neighborhoodId);

        for (String shiftPair : selectedShifts) {
            String[] shiftParts = shiftPair.split("-");

            long dayId = Long.parseLong(shiftParts[0]);
            long timeId = Long.parseLong(shiftParts[1]);

            Optional<Shift> existingShift = shiftDao.findShiftId(timeId, dayId);

            if (existingShift.isPresent()) {
                availabilityDao.createAvailability(amenity.getAmenityId(), existingShift.get().getShiftId());
            } else {
                Shift newShift = shiftDao.createShift(dayId, timeId);
                availabilityDao.createAvailability(amenity.getAmenityId(), newShift.getShiftId());
            }
        }

        return amenity;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Amenity> findAmenityById(long amenityId) {
        return amenityDao.findAmenityById(amenityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Amenity> getAmenities(long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Amenities from Neighborhood {}", neighborhoodId);
        return amenityDao.getAmenities(neighborhoodId, page, size);
    }

    @Override
    public int getAmenitiesCount(long neighborhoodId) {
        return amenityDao.getAmenitiesCount(neighborhoodId);
    }

    @Override
    public int getTotalAmenitiesPages(long neighborhoodId, int size) {
        return (int) Math.ceil((double) amenityDao.getAmenitiesCount(neighborhoodId) / size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteAmenity(long amenityId) {
        LOGGER.info("Deleting Amenity {}", amenityId);
        return amenityDao.deleteAmenity(amenityId);
    }
}
