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

    List<Request> getRequests(String userURN, String productURN, String typeURN, Boolean fulfilled, int page, int size, long neighborhoodId);

    int countRequests(String productURN, String userURN, String typeURN, Boolean fulfilled);

    int calculateRequestPages(String productURN, String userURN, String typeURN, Boolean fulfilled, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Request markRequestAsFulfilled(long requestId);
}
