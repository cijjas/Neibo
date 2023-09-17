package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Neighborhood;

import java.util.List;
import java.util.Optional;

public interface NeighborhoodDao {
    Neighborhood createNeighborhood(final String name);

    List<Neighborhood> getNeighborhoods();

    Optional<Neighborhood> findNeighborhoodById(long id);
}
