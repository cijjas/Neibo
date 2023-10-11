package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Neighborhood;

import java.util.List;
import java.util.Optional;

public interface NeighborhoodService {

    // -----------------------------------------------------------------------------------------------------------------

    Neighborhood createNeighborhood(final String name);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Neighborhood> findNeighborhoodById(long id);

    Optional<Neighborhood> findNeighborhoodByName(String name);

    List<Neighborhood> getNeighborhoods();
}
