package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Neighborhood;

import java.util.List;
import java.util.Optional;

public interface NeighborhoodService {

    Neighborhood createNeighborhood(final String name);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Neighborhood> findNeighborhood(long neighborhoodId);

    List<Neighborhood> getNeighborhoods(Long workerId, int size, int page);

    int calculateNeighborhoodPages(Long workerId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteNeighborhood(long neighborhoodId);
}
