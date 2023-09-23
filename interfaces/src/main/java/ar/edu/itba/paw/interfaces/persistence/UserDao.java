package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
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
    User createUser(final String mail, final String password, final String name, final String surname,
                    final long neighborhoodId, final String language, final boolean darkMode, final boolean verification, final String role);

    List<User> getUsers();

    Optional<User> findUserById(final long neighborId);

    public Optional<User> findUserByMail(final String mail);

    void setUserValues(final long id, final String name, final String surname, final String password, final boolean darkMode, final String language, final boolean verified, final String role);

    // ----------------------- Neighbor Methods
    User createNeighbor(final String mail, final String password, final String name, final String surname,
                        final long neighborhoodId, final String language, final boolean darkMode, final boolean verification);

    List<User> getNeighbors();

    List<User> getNeighborsByNeighborhood(long neighborhoodId);


    Optional<User> findNeighborByMail(String mail);

    List<User> getNeighborsSubscribedByPostId(long id);

    List<User> getNeighborsByNeighborhoodByVerification(long neighborhoodId, boolean verification);

    Optional<User> findNeighborById(long id);

    //List<Neighbor> getNeighborsByCommunity(long communityId);
}
