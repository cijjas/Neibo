package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Availability;

import java.util.List;
import java.util.Optional;

public interface AvailabilityService {

    Availability createAvailability(long amenityId, long shiftId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Availability> getAvailability(long amenityId);

    Optional<Availability> findAvailability(long id);

    boolean updateAvailability(long amenityId, List<String> newShifts);
}
