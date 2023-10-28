package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Availability;

import java.util.OptionalLong;

public interface AvailabilityDao {

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY INSERT -----------------------------------------

    Availability createAvailability(long amenityId, long shiftId);

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY SELECT -----------------------------------------

    OptionalLong findAvailabilityId(long amenityId, long shiftId);

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY DELETE -----------------------------------------

    boolean deleteAvailability(long amenityId, long shiftId);
}
