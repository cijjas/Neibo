package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.services.AvailabilityService;
import ar.edu.itba.paw.models.Entities.Availability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityServiceImpl.class);

    private final AvailabilityDao availabilityDao;

    @Autowired
    public AvailabilityServiceImpl(final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Availability> findAvailability(long shiftId, long amenityId) {
        LOGGER.info("Finding Availability from Amenity {} on Shift {}", amenityId, shiftId);

        return availabilityDao.findAvailability(amenityId, shiftId);
    }
}
