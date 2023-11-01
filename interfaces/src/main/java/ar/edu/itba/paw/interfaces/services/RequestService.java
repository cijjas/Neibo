package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.JunctionEntities.Request;

public interface RequestService {
    Request createRequest(final long userId, final long productId, final String message);
}
