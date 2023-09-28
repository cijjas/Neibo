package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.User;
import enums.Language;
import enums.UserRole;

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
                    final long neighborhoodId, final Language language, final boolean darkMode, final UserRole role);

    List<User> getUsers();

    Optional<User> findUserById(final long neighborId);

    public Optional<User> findUserByMail(final String mail);

    void setUserValues(final long id, final String password, final String name, final String surname, final Language language, final boolean darkMode, final UserRole role);



    // ----------------------- Neighbor Methods
    User createNeighbor(final String mail, final String password, final String name, final String surname,
                        final long neighborhoodId, final Language language, final boolean darkMode);

    List<User> getNeighbors();

    List<User> getNeighborsByNeighborhood(long neighborhoodId);


    Optional<User> findNeighborByMail(String mail);

    List<User> getNeighborsSubscribedByPostId(long id);

    List<User> getUnverifiedNeighborsByNeighborhood(long neighborhoodId);

    Optional<User> findNeighborById(long id);

    //List<Neighbor> getNeighborsByCommunity(long communityId);
}
