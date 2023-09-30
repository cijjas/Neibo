package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.DayTime;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AmenityDao {
    Amenity createAmenity(String name, String description, Map<String, DayTime> dayHourData);

    List<Amenity> getAmenities();

    Optional<Amenity> findAmenityById(long amenityId);

    boolean deleteAmenity(long amenityId);

    DayTime getAmenityHoursByDay(long amenityId, String dayOfWeek);

    Map<String, DayTime> getAmenityHoursByAmenityId(long amenityId);

    //boolean updateAmenity(long amenityId, String name, String description);
}
