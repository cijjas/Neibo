package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Request;

public interface RequestDao {
    Request createRequest(final long userId, final long productId);
}
