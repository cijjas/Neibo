package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Availability;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface AvailabilityService {

    Availability createAvailability(long amenityId, long shiftId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Availability> getAvailability(long amenityId);

    List<Availability> getAvailability(long amenityId, String status, String date);

    Optional<Availability> findAvailability(long availabilityId);

    Optional<Availability> findAvailability(long amenityId, long availabilityId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean updateAvailability(long amenityId, List<String> newShifts);
}
