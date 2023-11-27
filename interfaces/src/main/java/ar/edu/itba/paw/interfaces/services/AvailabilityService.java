package ar.edu.itba.paw.interfaces.services;

import java.util.List;

public interface AvailabilityService {
    boolean updateAvailability(long amenityId, List<String> newShifts);
}
