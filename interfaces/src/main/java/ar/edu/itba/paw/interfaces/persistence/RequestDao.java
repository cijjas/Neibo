package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestDao {

    // --------------------------------------------- REQUESTS INSERT ---------------------------------------------------

    Request createRequest(final long userId, final long productId, final String message, final int quantity);

    // --------------------------------------------- REQUESTS DELETE ---------------------------------------------------
    boolean deleteRequest(long requestId);

    // --------------------------------------------- REQUESTS SELECT ---------------------------------------------------

    Optional<Request> findRequest(long requestId);

    Optional<Request> findRequest(long requestId, long neighborhoodId);

    List<Request> getRequests(Long userId, Long productId, Long typeId, boolean fulfilled, int page, int size);

    int countRequests(Long userId, Long productId, Long typeId, boolean fulfilled);
}
