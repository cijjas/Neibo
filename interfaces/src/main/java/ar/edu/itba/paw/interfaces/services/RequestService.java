package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    Request createRequest(final long userId, final String productURN, final String message);

    boolean deleteRequest(long requestId);
    // -----------------------------------------------------------------------------------------------------------------

    Optional<Request> findRequest(long requestId);

    Optional<Request> findRequest(long requestId, long neighborhoodId);

    List<Request> getRequests(Long userId, Long productId, int page, int size, long neighborhoodId);

    int countRequests(Long productId, Long userId);

    int calculateRequestPages(Long productId, Long userId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Request markRequestAsFulfilled(long requestId);
}
