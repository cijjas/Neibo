package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Availability;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface AvailabilityDao {
    void createAvailability(long amenityId, long shiftId);

    Optional<Long> getAvailabilityId(long amenityId, long shiftId);
}
