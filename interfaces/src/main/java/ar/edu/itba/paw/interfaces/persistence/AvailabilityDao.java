package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Availability;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface AvailabilityDao {
    Number createAvailability(long amenityId, long shiftId);

    Optional<Long> findAvailabilityId(long amenityId, long shiftId);

    boolean deleteAvailability(long amenityId, long shiftId);
}
