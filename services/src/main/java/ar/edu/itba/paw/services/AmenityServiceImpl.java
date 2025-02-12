package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AmenityServiceImpl implements AmenityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityServiceImpl.class);

    private final AmenityDao amenityDao;
    private final AvailabilityDao availabilityDao;
    private final EmailService emailService;

    @Autowired
    public AmenityServiceImpl(AmenityDao amenityDao, AvailabilityDao availabilityDao, EmailService emailService) {
        this.availabilityDao = availabilityDao;
        this.amenityDao = amenityDao;
        this.emailService = emailService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Amenity createAmenity(long neighborhoodId, String name, String description, List<Long> selectedShiftsIds) {
        LOGGER.info("Creating Amenity {} described as {} with the following Shifts {} in Neighborhood {}", name, description, selectedShiftsIds, neighborhoodId);

        Amenity amenity = amenityDao.createAmenity(neighborhoodId, description, name);
        if (selectedShiftsIds != null)
            for (Long shiftId : selectedShiftsIds)
                availabilityDao.createAvailability(shiftId, amenity.getAmenityId());

        emailService.sendBatchNewAmenityMail(neighborhoodId, name, description);

        return amenity;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Amenity> findAmenity(long neighborhoodId, long amenityId) {
        LOGGER.info("Finding Amenity {} from Neighborhood {}", amenityId, neighborhoodId);

        return amenityDao.findAmenity(neighborhoodId, amenityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Amenity> getAmenities(long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Amenities from Neighborhood {}", neighborhoodId);

        return amenityDao.getAmenities(neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int countAmenities(long neighborhoodId) {
        LOGGER.info("Counting Amenities for Neighborhood {}", neighborhoodId);

        return amenityDao.countAmenities(neighborhoodId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Amenity updateAmenity(long neighborhoodId, long amenityId, String name, String description, List<Long> shiftIds) {
        LOGGER.info("Updating Amenity {} from Neighborhood {}", amenityId, neighborhoodId);

        Amenity amenity = amenityDao.findAmenity(neighborhoodId, amenityId).orElseThrow(NotFoundException::new);

        if (name != null && !name.isEmpty())
            amenity.setName(name);

        if (description != null && !description.isEmpty())
            amenity.setDescription(description);

        if (shiftIds != null) {
            List<Shift> currentShifts = amenity.getAvailableShifts();
            if (shiftIds.isEmpty())
                for (Shift shift : currentShifts)
                    availabilityDao.deleteAvailability(shift.getShiftId(), amenityId);
            else {
                Set<Long> currentShiftIds = currentShifts.stream()
                        .map(Shift::getShiftId)
                        .collect(Collectors.toSet());

                Set<Long> newShiftIds = new HashSet<>(shiftIds);

                Set<Long> shiftsToRemove = new HashSet<>(currentShiftIds);
                shiftsToRemove.removeAll(newShiftIds);

                Set<Long> shiftsToAdd = new HashSet<>(newShiftIds);
                shiftsToAdd.removeAll(currentShiftIds);

                for (Long id : shiftsToRemove)
                    availabilityDao.deleteAvailability(id, amenityId);

                for (Long id : shiftsToAdd)
                    availabilityDao.findAvailability(id, amenityId)
                            .orElseGet(() -> availabilityDao.createAvailability(id, amenityId));
            }
        }

        return amenity;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteAmenity(long neighborhoodId, long amenityId) {
        LOGGER.info("Deleting Amenity {} from Neighborhood {}", amenityId, neighborhoodId);

        return amenityDao.deleteAmenity(neighborhoodId, amenityId);
    }
}
