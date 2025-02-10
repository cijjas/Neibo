package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Amenity;

import java.util.List;
import java.util.Optional;

public interface AmenityService {

    Amenity createAmenity(long neighborhoodId, String name, String description, List<Long> selectedShiftsIds);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Amenity> findAmenity(long neighborhoodId, long amenityId);

    List<Amenity> getAmenities(long neighborhoodId, int page, int size);

    int countAmenities(long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Amenity updateAmenity(long neighborhoodId, long amenityId, String name, String description, List<Long> shiftIds);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAmenity(long neighborhoodId, long amenityId);
}
