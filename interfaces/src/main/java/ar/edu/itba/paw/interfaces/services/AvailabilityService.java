package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Availability;

import java.util.Optional;

public interface AvailabilityService {

    Optional<Availability> findAvailability(long shiftId, long amenityId);
}
