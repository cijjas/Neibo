package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Amenity;

import java.util.List;
import java.util.Optional;

public interface AmenityService {

    Amenity createAmenity(String name, String description, long neighborhoodId, List<String> selectedShiftsURNs);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Amenity> findAmenity(long amenityId);

    Optional<Amenity> findAmenity(long amenityId, long neighborhoodId);

    List<Amenity> getAmenities(long neighborhoodId, int page, int size);

    int calculateAmenityPages(long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Amenity updateAmenityPartially(long amenityId, String name, String description, List<String> shiftURNs);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAmenity(long amenityId);
}
