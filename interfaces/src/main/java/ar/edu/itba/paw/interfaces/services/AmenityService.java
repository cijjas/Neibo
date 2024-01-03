package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Amenity;

import java.util.List;
import java.util.Optional;

public interface AmenityService {

    Amenity createAmenity(String name, String description, long neighborhoodId, List<String> selectedShifts);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Amenity> findAmenityById(long amenityId);

    List<Amenity> getAmenities(long neighborhoodId, int page, int size);

    int getAmenitiesCount(long neighborhoodId);

    int getTotalAmenitiesPages(long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    void updateAmenity(long id, String name, String description);

    Amenity updateAmenityPartially(long id, String name, String description);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAmenity(long amenityId);

}
