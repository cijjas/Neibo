package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Amenity;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AmenityService {

    // -----------------------------------------------------------------------------------------------------------------

    Amenity createAmenity(String name, String description, Map<String, Map<Time, Time>> dayHourData);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Amenity> findAmenityById(long amenityId);

    List<Amenity> getAmenities();

    Map<Time, Time> getAmenityHoursByDay(long amenityId, String dayOfWeek);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAmenity(long amenityId);
}
