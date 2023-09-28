package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Amenity;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AmenityService {
    //mostrarle a admin dos timeboxes por cada dia de la semana, start and end -> ver como transformar eso en creacion de amenity
    Amenity createAmenity(String name, String description, Map<String, Map<Time, Time>> dayHourData);

    List<Amenity> getAmenities();

    Optional<Amenity> findAmenityById(long amenityId);

    boolean deleteAmenity(long amenityId);

    Map<Time, Time> getAmenityHoursByDay(long amenityId, String dayOfWeek);

    //boolean updateAmenity(long amenityId, String name, String description);
}
