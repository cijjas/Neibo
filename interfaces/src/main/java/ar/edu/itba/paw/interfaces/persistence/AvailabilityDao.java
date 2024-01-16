package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Availability;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public interface AvailabilityDao {

    // -------------------------------------------- AVAILABILITY INSERT ------------------------------------------------

    Availability createAvailability(long amenityId, long shiftId);

    // -------------------------------------------- AVAILABILITY SELECT ------------------------------------------------

    List<Availability> getAvailability(long amenityId);

    List<Availability> getAvailability(long amenityId, String status, String date);

    Optional<Availability> findAvailability(long availabilityId);

    Optional<Availability> findAvailability(long amenityId, long availabilityId);

    OptionalLong findId(long amenityId, long shiftId);

    // -------------------------------------------- AVAILABILITY DELETE ------------------------------------------------

    boolean deleteAvailability(long amenityId, long shiftId);
}
