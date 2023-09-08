package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.User;

public interface NeighborhoodDao {

    Neighborhood create(final String name);
}
