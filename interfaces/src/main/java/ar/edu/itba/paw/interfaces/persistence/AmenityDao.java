package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Amenity;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AmenityDao {
    Amenity createAmenity(String name, String description, Map<String, Map<Time, Time>> dayHourData);

    List<Amenity> getAmenities();

    Optional<Amenity> findAmenityById(long amenityId);

    boolean deleteAmenity(long amenityId);

    Map<Time, Time> getAmenityHoursByDay(long amenityId, String dayOfWeek);

    //boolean updateAmenity(long amenityId, String name, String description);
}
