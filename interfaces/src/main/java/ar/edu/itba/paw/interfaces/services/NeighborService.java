package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface NeighborService {
    /*
        Use descriptive names that indicate the purpose of the method.
        Prefix service layer methods with verbs like "create," "retrieve," "update," or "delete" to indicate their primary action.
        Use camelCase or PascalCase for method names, depending on your project's convention.
        Include domain-specific terms when applicable.
        Examples:
            createUserAccount
            getUserProfile
            updateProductDetails
            deleteOrder
     */

    Neighbor createNeighbor(final String email, final String name, final String surname, final long neighborhoodId);

    List<Neighbor> getNeighbors();

    List<Neighbor> getNeighborsByNeighborhood(long neighborhoodId);

    Optional<Neighbor> findNeighborByMail(String mail);

    List<Neighbor> getNeighborsSubscribedByPostId(long id);

    Optional<Neighbor> findNeighborById(long id);

    //List<Neighbor> getNeighborsByCommunity(long communityId);
}
