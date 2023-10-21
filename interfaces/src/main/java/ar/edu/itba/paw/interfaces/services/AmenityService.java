package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Amenity;

import java.util.List;
import java.util.Optional;

public interface AmenityService {

    Amenity createAmenity(String name, String description, Long neighborhoodId, List<String> selectedShifts);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Amenity> findAmenityById(Long amenityId);

    List<Amenity> getAmenities(Long neighborhoodId, int page, int size);

    int getAmenitiesCount(Long neighborhoodId);

    int getTotalAmenitiesPages(Long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAmenity(Long amenityId);

}
