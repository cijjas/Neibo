package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestDao {

    // --------------------------------------------- REQUESTS INSERT ---------------------------------------------------

    Request createRequest(long userId, long productId, String message, int quantity);

    // --------------------------------------------- REQUESTS SELECT ---------------------------------------------------

    Optional<Request> findRequest(long requestId);

    List<Request> getRequests(long neighborhoodId, Long userId, Long productId, Long typeId, Long statusId, int page, int size);

    int countRequests(long neighborhoodId, Long userId, Long productId, Long typeId, Long statusId);

    // --------------------------------------------- REQUESTS DELETE ---------------------------------------------------

    boolean deleteRequest(long neighborhoodId, long requestId);
}
