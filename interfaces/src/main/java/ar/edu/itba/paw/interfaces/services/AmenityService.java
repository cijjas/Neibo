package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.DayTime;
import ar.edu.itba.paw.models.Shift;
import enums.StandardTime;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AmenityService {
    Amenity createAmenity(String name, String description, long neighborhoodId, List<String> selectedShifts);

    boolean deleteAmenity(long amenityId);

    Optional<Amenity> findAmenityById(long amenityId);

    List<Amenity> getAmenities(long neighborhoodId);
}
