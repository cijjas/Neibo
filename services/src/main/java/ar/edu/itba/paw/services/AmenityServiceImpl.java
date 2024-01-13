package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AmenityServiceImpl implements AmenityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityServiceImpl.class);
    private final AmenityDao amenityDao;
    private final ShiftDao shiftDao;
    private final AvailabilityDao availabilityDao;
    private final EmailService emailService;

    private final UserService userService;

    @Autowired
    public AmenityServiceImpl(final AmenityDao amenityDao, final ShiftDao shiftDao, final AvailabilityDao availabilityDao, final EmailService emailService, UserService userService) {
        this.availabilityDao = availabilityDao;
        this.shiftDao = shiftDao;
        this.amenityDao = amenityDao;
        this.emailService = emailService;
        this.userService = userService;
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

        List<User> userlist = userService.getNeighbors(neighborhoodId);

        emailService.sendNewAmenityMail(neighborhoodId, name, description, userlist);

        return amenity;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Amenity> findAmenityById(long amenityId) {
        LOGGER.info("Finding Amenity with id {}", amenityId);

        ValidationUtils.checkAmenityId(amenityId);

        return amenityDao.findAmenityById(amenityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Amenity> getAmenities(long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Amenities from Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        return amenityDao.getAmenities(neighborhoodId, page, size);
    }

    @Override
    public int getTotalAmenitiesPages(long neighborhoodId, int size) {

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return (int) Math.ceil((double) amenityDao.getAmenitiesCount(neighborhoodId) / size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void updateAmenity(long amenityId, String name, String description){

        ValidationUtils.checkAmenityId(amenityId);

        Amenity amenity = amenityDao.findAmenityById(amenityId).orElseThrow(()-> new NotFoundException("Amenity Not Found"));
        amenity.setName(name);
        amenity.setDescription(description);
    }

    @Override
    public Amenity updateAmenityPartially(long amenityId, String name, String description){

        ValidationUtils.checkAmenityId(amenityId);

        Amenity amenity = amenityDao.findAmenityById(amenityId).orElseThrow(()-> new NotFoundException("Amenity Not Found"));
        if(name != null && !name.isEmpty())
            amenity.setName(name);
        if(description != null && !description.isEmpty())
            amenity.setDescription(description);
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
