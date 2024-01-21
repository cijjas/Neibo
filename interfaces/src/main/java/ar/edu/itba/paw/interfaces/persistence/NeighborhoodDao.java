package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Neighborhood;

import java.util.List;
import java.util.Optional;

public interface NeighborhoodDao {

    // ----------------------------------------- NEIGHBORHOODS INSERT --------------------------------------------------

    Neighborhood createNeighborhood(final String name);

    // ----------------------------------------- NEIGHBORHOODS SELECT --------------------------------------------------

    Optional<Neighborhood> findNeighborhood(long neighborhoodId);

    Optional<Neighborhood> findNeighborhood(String name);

    List<Neighborhood> getNeighborhoods();

    List<Neighborhood> getNeighborhoods(int page, int size);

    // ---------------------------------------------------

    int getNeighborhoodsCount();

}
