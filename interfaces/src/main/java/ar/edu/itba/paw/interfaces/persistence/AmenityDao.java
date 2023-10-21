package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Amenity;

import java.util.List;
import java.util.Optional;

public interface AmenityDao {

    // --------------------------------------------- AMENITIES INSERT --------------------------------------------------

    Amenity createAmenity(String name, String description, Long neighborhoodId);

    // --------------------------------------------- AMENITIES SELECT --------------------------------------------------

    Optional<Amenity> findAmenityById(Long amenityId);

    List<Amenity> getAmenities(Long neighborhoodId, int page, int size);

    int getAmenitiesCount(Long neighborhoodId);

    // --------------------------------------------- AMENITIES DELETE --------------------------------------------------

    boolean deleteAmenity(Long amenityId);
}
