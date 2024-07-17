package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    Request createRequest(final long userId, final String productURN, final String message, final int quantity);

    boolean deleteRequest(long requestId);
    // -----------------------------------------------------------------------------------------------------------------

    Optional<Request> findRequest(long requestId);

    Optional<Request> findRequest(long requestId, long neighborhoodId);

    List<Request> getRequests(String userURN, String productURN, int page, int size, long neighborhoodId);

    int countRequests(String productURN, String userURN);

    int calculateRequestPages(String productURN, String userURN, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Request markRequestAsFulfilled(long requestId);
}
