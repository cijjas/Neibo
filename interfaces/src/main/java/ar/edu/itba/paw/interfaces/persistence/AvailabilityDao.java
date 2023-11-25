package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Availability;

import java.util.OptionalLong;

public interface AvailabilityDao {

    // -------------------------------------------- AVAILABILITY INSERT ------------------------------------------------

    Availability createAvailability(long amenityId, long shiftId);

    // -------------------------------------------- AVAILABILITY SELECT ------------------------------------------------

    OptionalLong findAvailabilityId(long amenityId, long shiftId);

    // -------------------------------------------- AVAILABILITY DELETE ------------------------------------------------

    boolean deleteAvailability(long amenityId, long shiftId);
}
