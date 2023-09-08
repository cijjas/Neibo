package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface NeighborDao {
    User create(final String email, final String password);

    Neighbor create2(final String email, final String name, final String surname, final int neighborhoodId);

    Optional<User> findById(long id);
}
