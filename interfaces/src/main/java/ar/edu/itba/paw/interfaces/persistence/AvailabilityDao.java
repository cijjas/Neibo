package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Availability;

import java.util.Optional;

public interface AvailabilityDao {

    // -------------------------------------------- AVAILABILITY INSERT ------------------------------------------------

    Availability createAvailability(long shiftId, long amenityId);

    // -------------------------------------------- AVAILABILITY SELECT ------------------------------------------------

    Optional<Availability> findAvailability(long shiftId, long amenityId);

    // -------------------------------------------- AVAILABILITY DELETE ------------------------------------------------

    boolean deleteAvailability(long shiftId, long amenityId);
}
