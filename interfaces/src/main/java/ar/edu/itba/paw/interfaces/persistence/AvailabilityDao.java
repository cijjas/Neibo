package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Availability;

import java.util.Optional;

public interface AvailabilityDao {

    // -------------------------------------------- AVAILABILITY INSERT ------------------------------------------------

    Availability createAvailability(long amenityId, long shiftId);

    // -------------------------------------------- AVAILABILITY SELECT ------------------------------------------------

    Optional<Availability> findAvailability(long amenityId, long shiftId);

    // -------------------------------------------- AVAILABILITY DELETE ------------------------------------------------

    boolean deleteAvailability(long amenityId, long shiftId);
}
