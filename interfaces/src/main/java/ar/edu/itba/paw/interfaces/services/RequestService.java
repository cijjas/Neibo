package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.JunctionEntities.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(final long userId, final long productId, final String message);

    List<Request> getRequestsByProductId(long productId, int page, int size);

    int getRequestsCountByProductId(long productId);

    List<Request> getRequestsByProductAndUser(long productId, long userId, int page, int size);

    int getRequestsCountByProductAndUser(long productId, long userId);

    void markRequestAsFulfilled(long requestId);
}
