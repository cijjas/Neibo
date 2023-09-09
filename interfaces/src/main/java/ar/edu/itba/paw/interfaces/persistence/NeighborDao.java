package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface NeighborDao {

    Neighbor create(final String email, final String name, final String surname, final long neighborhoodId);

    Optional<User> findById(long id);

}
