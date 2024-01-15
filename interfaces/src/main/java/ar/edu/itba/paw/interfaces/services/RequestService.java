package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(final long userId, final long productId, final String message);

    // -----------------------------------------------------------------------------------------------------------------

    List<Request> getRequests(Long productId, Long userId, int page, int size);

    int countRequests(Long productId, Long userId);

    int calculateRequestPages(Long productId, Long userId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    void markRequestAsFulfilled(long requestId);
}
