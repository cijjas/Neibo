package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Neighbor;

import java.util.List;
import java.util.Optional;

public interface NeighborDao {
    /*
    Use descriptive names that include the entity or data type being manipulated.
    Prefix DAO methods with verbs like "save," "find," "update," or "delete" to indicate their primary action.
    Use camelCase for method names.
    Examples:
        saveUser
        findProductById
        updateOrderStatus
        deleteCustomer
     */
    Neighbor createNeighbor(final String email, final String name, final String surname, final long neighborhoodId);

    List<Neighbor> getNeighbors();

    List<Neighbor> getNeighborsByNeighborhood(long neighborhoodId);


    Optional<Neighbor> findNeighborByMail(String mail);

    List<Neighbor> getNeighborsSubscribedByPostId(long id);

    Optional<Neighbor> findNeighborById(long id);

    //List<Neighbor> getNeighborsByCommunity(long communityId);
}
