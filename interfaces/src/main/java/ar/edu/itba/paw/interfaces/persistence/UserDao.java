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

    Optional<User> findUser(final long userId);

    Optional<User> findUser(final long userId, long neighborhoodId);

    Optional<User> findUser(final String mail);

    List<User> getUsers(Long userRoleId, long neighborhoodId, int page, int size);

    List<User> getNeighborsSubscribed(long postId);

    int countTotalUsers(Long userRoleId, long neighborhoodId);

    List<User> getEventUsers(long eventId);

    List<User> getEventUsers(long eventId, int page, int size);

    boolean isAttending(long eventId, long userId);

    List<User> getProductRequesters(long productId, int page, int size);

    boolean deleteUser(long userId);
}
