package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface NeighborService {
    User createUser(final String email, final String password);

    Optional<User> findById(long id);


    Neighbor createNeighbor(final String email, final String name, final String surname, final long neighborhoodId);


}
