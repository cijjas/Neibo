package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.DayTime;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AmenityService {
    //mostrarle a admin dos timeboxes por cada dia de la semana, start and end -> ver como transformar eso en creacion de amenity
    Amenity createAmenity(String name, String description, Map<String, DayTime> dayHourData);

    List<Amenity> getAmenities();

    Optional<Amenity> findAmenityById(long amenityId);

    boolean deleteAmenity(long amenityId);

    DayTime getAmenityHoursByDay(long amenityId, String dayOfWeek);

    Map<String, DayTime> getAmenityHoursByAmenityId(long amenityId);

    //boolean updateAmenity(long amenityId, String name, String description);
    Amenity createAmenityWrapper(String name, String description, Time mondayOpenTime, Time mondayCloseTime, Time tuesdayOpenTime, Time tuesdayCloseTime, Time wednesdayOpenTime, Time wednesdayCloseTime, Time thursdayOpenTime, Time thursdayCloseTime, Time fridayOpenTime, Time fridayCloseTime, Time saturdayOpenTime, Time saturdayCloseTime, Time sundayOpenTime, Time sundayCloseTime);
}
