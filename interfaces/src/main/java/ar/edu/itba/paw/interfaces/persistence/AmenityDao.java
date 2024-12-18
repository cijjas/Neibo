package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Amenity;

import java.util.List;
import java.util.Optional;

public interface AmenityDao {

    // --------------------------------------------- AMENITIES INSERT --------------------------------------------------

    Amenity createAmenity(long neighborhoodId, String description, String name);

    // --------------------------------------------- AMENITIES SELECT --------------------------------------------------

    Optional<Amenity> findAmenity(long amenityId);

    Optional<Amenity> findAmenity(long neighborhoodId, long amenityId);

    List<Amenity> getAmenities(long neighborhoodId, int page, int size);

    int countAmenities(long neighborhoodId);

    // --------------------------------------------- AMENITIES DELETE --------------------------------------------------

    boolean deleteAmenity(long neighborhoodId, long amenityId);
}
