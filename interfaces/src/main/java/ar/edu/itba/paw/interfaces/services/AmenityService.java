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

    // -----------------------------------------------------------------------------------------------------------------

    Amenity createAmenity(String name, String description, Map<String, DayTime> dayHourData, long neighborhoodId);

    Amenity createAmenityWrapper(String name, String description, Time mondayOpenTime, Time mondayCloseTime, Time tuesdayOpenTime,
                                 Time tuesdayCloseTime, Time wednesdayOpenTime, Time wednesdayCloseTime, Time thursdayOpenTime,
                                 Time thursdayCloseTime, Time fridayOpenTime, Time fridayCloseTime, Time saturdayOpenTime, Time saturdayCloseTime,
                                 Time sundayOpenTime, Time sundayCloseTime, long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Amenity> findAmenityById(long amenityId);

    List<Amenity> getAmenities(long neighborhoodId);

    DayTime getAmenityHoursByDay(long amenityId, String dayOfWeek);

    Map<String, DayTime> getAmenityHoursByAmenityId(long amenityId);

    Map<Amenity, List<Shift>> getAllAmenitiesIdWithListOfShifts(long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAmenity(long amenityId);
    boolean deleteAmenity2(long amenityId);

    List<Time> getAllTimes();

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // JOACO
    Amenity createAmenity(String name, String description, long neighborhoodId, List<String> selectedShifts);
}
