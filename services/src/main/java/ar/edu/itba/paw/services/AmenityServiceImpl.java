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
    private final UserDao userDao;

    @Autowired
    public AmenityServiceImpl(final AmenityDao amenityDao, final AvailabilityDao availabilityDao,
                              final EmailService emailService, UserDao userDao) {
        this.availabilityDao = availabilityDao;
        this.amenityDao = amenityDao;
        this.emailService = emailService;
        this.userDao = userDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, long neighborhoodId, List<Long> selectedShiftsIds) {
        LOGGER.info("Creating Amenity {}", name);

        Amenity amenity = amenityDao.createAmenity(name, description, neighborhoodId);
        for (Long shiftId : selectedShiftsIds) {
            availabilityDao.createAvailability(amenity.getAmenityId(), shiftId);
        }

        emailService.sendBatchNewAmenityMail(neighborhoodId, name, description);

        // todo: This should be refactored into using the sendBatchEmail method, and by consequence the service test >:(
        int size = 500;
        int totalPages = PaginationUtils.calculatePages(userDao.countUsers((long) UserRole.NEIGHBOR.getId(), neighborhoodId), size);
        for (int page = 1; page <= totalPages; page++) {
            List<User> users = userDao.getUsers((long) UserRole.NEIGHBOR.getId(), neighborhoodId, page, size);
            if (!users.isEmpty())
                emailService.sendNewAmenityMail(neighborhoodId, name, description, users);
        }

        return amenity;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Amenity> findAmenity(long amenityId, long neighborhoodId) {
        LOGGER.info("Finding Amenity {} from Neighborhood {}", amenityId, neighborhoodId);

        return amenityDao.findAmenity(amenityId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Amenity> getAmenities(long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Amenities from Neighborhood {}", neighborhoodId);

        return amenityDao.getAmenities(neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateAmenityPages(long neighborhoodId, int size) {
        LOGGER.info("Calculating Amenity Pages for Neighborhood {}", neighborhoodId);

        return PaginationUtils.calculatePages(amenityDao.countAmenities(neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Amenity updateAmenityPartially(long amenityId, String name, String description, List<Long> shiftIds) {
        LOGGER.info("Updating Amenity {}", amenityId);

        Amenity amenity = amenityDao.findAmenity(amenityId).orElseThrow(NotFoundException::new);

        if (name != null && !name.isEmpty())
            amenity.setName(name);

        if (description != null && !description.isEmpty())
            amenity.setDescription(description);

        if (shiftIds != null) {
            List<Shift> currentShifts = amenity.getAvailableShifts();
            if (shiftIds.isEmpty()) // If the provided shiftIds list is empty, remove all availabilities for the amenity
                for (Shift shift : currentShifts)
                    availabilityDao.deleteAvailability(amenityId, shift.getShiftId());
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
                    availabilityDao.deleteAvailability(amenityId, id);

                for (Long id : shiftsToAdd)
                    availabilityDao.findAvailability(amenityId, id)
                            .orElseGet(() -> availabilityDao.createAvailability(amenityId, id));
            }
        }

        return amenity;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteAmenity(long amenityId) {
        LOGGER.info("Deleting Amenity {}", amenityId);

        return amenityDao.deleteAmenity(amenityId);
    }
}
