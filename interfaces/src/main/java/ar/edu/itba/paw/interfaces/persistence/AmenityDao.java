package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.DayTime;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AmenityDao {

    // --------------------------------------------- AMENITIES INSERT --------------------------------------------------

    Amenity createAmenity(String name, String description, Map<String, DayTime> dayHourData);

    // --------------------------------------------- AMENITIES INSERT --------------------------------------------------

    Optional<Amenity> findAmenityById(long amenityId);

    List<Amenity> getAmenities();

    DayTime getAmenityHoursByDay(long amenityId, String dayOfWeek);

    Map<String, DayTime> getAmenityHoursByAmenityId(long amenityId);

    // --------------------------------------------- AMENITIES INSERT --------------------------------------------------

    boolean deleteAmenity(long amenityId);
}
