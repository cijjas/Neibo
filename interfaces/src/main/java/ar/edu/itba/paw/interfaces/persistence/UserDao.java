package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.Entities.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    // ---------------------------------------------- USERS INSERT -----------------------------------------------------

    User createUser(final String mail, final String password, final String name, final String surname,
                    final long neighborhoodId, final Language language, final boolean darkMode, final UserRole role, final int identification);

    // ---------------------------------------------- USERS SELECT -----------------------------------------------------

    Optional<User> findUserById(final long neighborId);

    Optional<User> findUserByMail(final String mail);

    List<User> getUsersByCriteria(UserRole role, long neighborhoodId, int page, int size);

    List<User> getNeighborsSubscribedByPostId(long id);

    int getTotalUsers(UserRole role, long neighborhoodId);

    List<User> getEventUsers(long eventId);

    List<User> getEventUsersByCriteria(long eventId, int page, int size);

    boolean isAttending(long eventId, long userId);

    List<User> getProductRequesters(long productId, int page, int size);
}
