package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestService {

    Request createRequest(long userId, long productId, String message, int quantity);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Request> findRequest(long requestId); // Used for Access Control

    Optional<Request> findRequest(long neighborhoodId, long requestId);

    List<Request> getRequests(long neighborhoodId, Long userId, Long productId, Long requestStatusId, Long transactionTypeId, int page, int size);

    int countRequests(long neighborhoodId, Long userId, Long productId, Long requestStatusId, Long transactionTypeId);

    int calculateRequestPages(long neighborhoodId, Long userId, Long productId, Long requestStatusId, Long transactionTypeId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Request updateRequest(long neighborhoodId, long requestId, Long requestStatusId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteRequest(long neighborhoodId, long requestId);
}
