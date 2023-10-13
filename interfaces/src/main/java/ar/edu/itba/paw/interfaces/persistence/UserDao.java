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

    // ---------------------------------------------- USERS INSERT -----------------------------------------------------

    User createUser(final String mail, final String password, final String name, final String surname,
                    final long neighborhoodId, final Language language, final boolean darkMode, final UserRole role, final int identification) ;

    // ---------------------------------------------- USERS SELECT -----------------------------------------------------

    Optional<User> findUserById(final long neighborId);

    Optional<User> findUserByMail(final String mail);

    List<User> getUsersByCriteria(UserRole role, long neighborhoodId, int page, int size);

    List<User> getNeighborsSubscribedByPostId(long id);

    int getTotalUsers(UserRole role, long neighborhoodId);

    List<User> getEventUsers(long eventId);

    boolean isAttending(long eventId, long userId);

    // ---------------------------------------------- USERS UPDATE -----------------------------------------------------

    void setUserValues(final long id, final String password, final String name, final String surname,
                       final Language language, final boolean darkMode, final long profilePictureId,
                       final UserRole role, final int identification, final long neighborhoodId);
}
