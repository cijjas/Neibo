package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.MainEntities.Amenity;

import java.util.List;
import java.util.Optional;

public interface AmenityDao {

    // --------------------------------------------- AMENITIES INSERT --------------------------------------------------

    Amenity createAmenity(String name, String description, long neighborhoodId);

    // --------------------------------------------- AMENITIES SELECT --------------------------------------------------

    Optional<Amenity> findAmenityById(long amenityId);

    List<Amenity> getAmenities(long neighborhoodId, int page, int size);

    int getAmenitiesCount(long neighborhoodId);

    // --------------------------------------------- AMENITIES UPDATE --------------------------------------------------

    void updateAmenity(final long id, final String name, final String description);

    // --------------------------------------------- AMENITIES DELETE --------------------------------------------------

    boolean deleteAmenity(long amenityId);
}
