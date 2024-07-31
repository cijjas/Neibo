package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Amenity;

import java.util.List;
import java.util.Optional;

public interface AmenityDao {

    // --------------------------------------------- AMENITIES INSERT --------------------------------------------------

    Amenity createAmenity(String name, String description, long neighborhoodId);

    // --------------------------------------------- AMENITIES SELECT --------------------------------------------------

    Optional<Amenity> findAmenity(long amenityId);

    Optional<Amenity> findAmenity(long amenityId, long neighborhoodId);

    List<Amenity> getAmenities(long neighborhoodId, int page, int size);

    int countAmenities(long neighborhoodId);

    // --------------------------------------------- AMENITIES DELETE --------------------------------------------------

    boolean deleteAmenity(long amenityId);
}
