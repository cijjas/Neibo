package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestService {

    Request createRequest(final long userId, final long productId, final String message, final int quantity);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Request> findRequest(long requestId);

    Optional<Request> findRequest(long requestId, long neighborhoodId);

    List<Request> getRequests(Long userId, Long productId, Long transactionTypeId, Long requestStatusId, int page, int size, long neighborhoodId);

    // ---------------------------------------------------

    int calculateRequestPages(Long productId, Long userId, Long transactionTypeURN, Long requestStatusURN, long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Request updateRequest(long requestId, Long requestStatusId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteRequest(long requestId);
}
