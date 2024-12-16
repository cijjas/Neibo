package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestDao {

    // --------------------------------------------- REQUESTS INSERT ---------------------------------------------------

    Request createRequest(final long userId, final long productId, final String message, final int quantity);

    // --------------------------------------------- REQUESTS SELECT ---------------------------------------------------

    Optional<Request> findRequest(long requestId);

    List<Request> getRequests(Long userId, Long productId, Long typeId, Long statusId, long neighborhoodId, int page, int size);

    int countRequests(Long userId, Long productId, Long typeId, Long statusId, long neighborhoodId);

    // --------------------------------------------- REQUESTS DELETE ---------------------------------------------------

    boolean deleteRequest(long neighborhoodId, long requestId);
}
