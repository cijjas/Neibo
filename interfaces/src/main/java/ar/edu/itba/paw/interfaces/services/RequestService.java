package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestService {

    Request createRequest(final long userId, final long productId, final String message, final int quantity);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Request> findRequest(long requestId);

    Optional<Request> findRequest(long requestId, long neighborhoodId);

    List<Request> getRequests(String userURN, String productURN, String typeURN, String statusURN, int page, int size, long neighborhoodId);

    // ---------------------------------------------------

    int calculateRequestPages(String productURN, String userURN, String typeURN, String statusURN, long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Request updateRequest(long requestId, String requestStatusURN);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteRequest(long requestId);
}
