package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestDao {

    // --------------------------------------------- REQUESTS INSERT ---------------------------------------------------

    Request createRequest(final long userId, final long productId, final String message);

    // --------------------------------------------- REQUESTS SELECT ---------------------------------------------------

    Optional<Request> findRequest(long requestId);

    List<Request> getRequestsByProductId(long productId, int page, int size);

    int getRequestsCountByProductId(long productId);

    List<Request> getRequestsByProductAndUser(long productId, long userId, int page, int size);

    int getRequestsCountByProductAndUser(long productId, long userId);

    List<Request> getRequestsByUserId(long userId, int page, int size);

    int getRequestsCountByUserId(long userId);
}
