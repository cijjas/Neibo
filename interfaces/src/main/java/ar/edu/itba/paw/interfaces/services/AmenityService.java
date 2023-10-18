package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Amenity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ar.edu.itba.paw.models.Amenity;

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

    boolean deleteAmenity(long amenityId);

}
