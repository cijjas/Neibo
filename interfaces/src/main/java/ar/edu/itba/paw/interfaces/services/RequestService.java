package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(final long userId, final long productId, final String message);

    // -----------------------------------------------------------------------------------------------------------------

    List<Request> getRequestsByCriteria(long productId, long userId, int page, int size);

    int getRequestsCountByCriteria(long productId, long userId);

    List<Request> getRequestsByProductId(long productId, int page, int size);

    int getRequestsCountByProductId(long productId);

    List<Request> getRequestsByProductAndUser(long productId, long userId, int page, int size);

    List<Request> getRequestsByUserId(long userId, int page, int size);

    int getRequestsCountByProductAndUser(long productId, long userId);

    int getRequestsCountUser(long userId);

    // -----------------------------------------------------------------------------------------------------------------

    void markRequestAsFulfilled(long requestId);
}
