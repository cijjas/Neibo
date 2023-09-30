package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Amenity;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AmenityDao {

    // ---------------------------------------------- AMENITY INSERT ---------------------------------------------------

    Amenity createAmenity(String name, String description, Map<String, Map<Time, Time>> dayHourData);

    // ---------------------------------------------- AMENITY SELECT ---------------------------------------------------

    Optional<Amenity> findAmenityById(long amenityId);

    List<Amenity> getAmenities();

    Map<Time, Time> getAmenityHoursByDay(long amenityId, String dayOfWeek);

    // ---------------------------------------------- AMENITY DELETE ---------------------------------------------------

    boolean deleteAmenity(long amenityId);
}
