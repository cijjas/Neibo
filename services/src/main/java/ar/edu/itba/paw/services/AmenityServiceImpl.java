package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.*;
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

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AmenityServiceImpl implements AmenityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityServiceImpl.class);

    private final AmenityDao amenityDao;
    private final ShiftDao shiftDao;
    private final AvailabilityDao availabilityDao;
    private final NeighborhoodDao neighborhoodDao;
    private final EmailService emailService;
    private final UserDao userDao;

    @Autowired
    public AmenityServiceImpl(final AmenityDao amenityDao, final ShiftDao shiftDao, final AvailabilityDao availabilityDao,
                              final EmailService emailService, final NeighborhoodDao neighborhoodDao, UserDao userDao) {
        this.availabilityDao = availabilityDao;
        this.shiftDao = shiftDao;
        this.amenityDao = amenityDao;
        this.emailService = emailService;
        this.neighborhoodDao = neighborhoodDao;
        this.userDao = userDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Function has to create the shifts if they do not already exist and match them with the amenity through the junction table
    @Override
    public Amenity createAmenity(String name, String description, long neighborhoodId, List<Long> selectedShiftsIds) {
        LOGGER.info("Creating Amenity {}", name);

        Amenity amenity = amenityDao.createAmenity(name, description, neighborhoodId);
        for (Long shiftId: selectedShiftsIds)
            availabilityDao.createAvailability(amenity.getAmenityId(), shiftId);

        emailService.sendBatchNewAmenityMail(neighborhoodId, name, description);

        int page = 1;
        int size = 500; // Will fetch and send emails to 500 users at a time
        List<User> users;
        do {
            // Fetch users in batches
            users = userDao.getUsers((long) UserRole.NEIGHBOR.getId(), neighborhoodId, page, size);

            // Send email with the current batch of users
            if (!users.isEmpty()) {
                emailService.sendNewAmenityMail(neighborhoodId, name, description, users);
            }

            page++;
        } while (users.size() == size); // Continue fetching next page if the current page is full

        return amenity;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Amenity> findAmenity(long amenityId) {
        LOGGER.info("Finding Amenity Amenity {}", amenityId);

        ValidationUtils.checkAmenityId(amenityId);

        return amenityDao.findAmenity(amenityId);
    }

    @Override
    public Optional<Amenity> findAmenity(long amenityId, long neighborhoodId) {
        LOGGER.info("Finding Amenity {} from Neighborhood {}", amenityId, neighborhoodId);

        ValidationUtils.checkAmenityId(amenityId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return amenityDao.findAmenity(amenityId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Amenity> getAmenities(long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Amenities from Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return amenityDao.getAmenities(neighborhoodId, page, size);
    }

    @Override
    public int calculateAmenityPages(long neighborhoodId, int size) {
        LOGGER.info("Calculating Amenity Pages for Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(amenityDao.countAmenities(neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Amenity updateAmenityPartially(long amenityId, String name, String description, List<Long> shiftIds) {
        LOGGER.info("Updating Amenity {}", amenityId);

        Amenity amenity = amenityDao.findAmenity(amenityId).orElseThrow(() -> new NotFoundException("Amenity Not Found"));
        if (name != null && !name.isEmpty())
            amenity.setName(name);
        if (description != null && !description.isEmpty())
            amenity.setDescription(description);
        if (shiftIds != null && !shiftIds.isEmpty()) {
            for (Long shiftId: shiftIds) {
                // todo logica para remover los que no estan en este update?
                availabilityDao.findAvailability(amenityId, shiftId).orElseGet(() -> availabilityDao.createAvailability(amenityId, shiftId));
            }
        }
        return amenity;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteAmenity(long amenityId) {
        LOGGER.info("Deleting Amenity {}", amenityId);

        ValidationUtils.checkAmenityId(amenityId);

        return amenityDao.deleteAmenity(amenityId);
    }
}
