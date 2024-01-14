package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Neighborhood;

import java.util.List;
import java.util.Optional;

public interface NeighborhoodService {

    Neighborhood createNeighborhood(final String name);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Neighborhood> findNeighborhood(long neighborhoodId);

    Optional<Neighborhood> findNeighborhood(String name);

    List<Neighborhood> getNeighborhoods();

    List<Neighborhood> getNeighborhoods(int page, int size);

    // ---------------------------------------------------

    int countNeighborhoods();

    int calculateNeighborhoodPages(int size);
}
