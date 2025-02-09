package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Neighborhood;

import java.util.List;
import java.util.Optional;

public interface NeighborhoodDao {

    // ----------------------------------------- NEIGHBORHOODS INSERT --------------------------------------------------

    Neighborhood createNeighborhood(String name);

    // ----------------------------------------- NEIGHBORHOODS SELECT --------------------------------------------------

    Optional<Neighborhood> findNeighborhood(long neighborhoodId);

    List<Neighborhood> getNeighborhoods(Boolean isBase, Long withWorkerId, Long withoutWorkerId, int page, int size);

    int countNeighborhoods(Boolean isBase, Long withWorkerId, Long withoutWorkerId);

    // ----------------------------------------- NEIGHBORHOODS DELETE --------------------------------------------------

    boolean deleteNeighborhood(long neighborhoodId);

}
