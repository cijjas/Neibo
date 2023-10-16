package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Availability;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface AvailabilityDao {

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY INSERT -----------------------------------------

    Number createAvailability(long amenityId, long shiftId);

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY SELECT -----------------------------------------

    Optional<Long> findAvailabilityId(long amenityId, long shiftId);

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY DELETE -----------------------------------------

    boolean deleteAvailability(long amenityId, long shiftId);
}
