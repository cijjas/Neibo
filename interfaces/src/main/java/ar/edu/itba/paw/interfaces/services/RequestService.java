package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    Request createRequest(final long userId, final long productId, final String message);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Request> findRequest(long requestId);

    Optional<Request> findRequest(long requestId, long neighborhoodId);

    List<Request> getRequests(Long productId, Long userId, int page, int size);

    int countRequests(Long productId, Long userId);

    int calculateRequestPages(Long productId, Long userId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    void markRequestAsFulfilled(long requestId);
}
