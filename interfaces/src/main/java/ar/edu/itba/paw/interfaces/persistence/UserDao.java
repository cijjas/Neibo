package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.Entities.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    // ---------------------------------------------- USERS INSERT -----------------------------------------------------

    User createUser(long neighborhoodId, String mail, String name, String surname, String password, int identification, Language language, boolean darkMode, UserRole role);

    // ---------------------------------------------- USERS SELECT -----------------------------------------------------

    Optional<User> findUser(long neighborhoodId, long userId);

    Optional<User> findUser(long userId);

    Optional<User> findUser(String mail);

    List<User> getUsers(long neighborhoodId, Long userRoleId, int page, int size);

    int countUsers(long neighborhoodId, Long userRoleId);

    List<User> getEventUsers(long eventId);
}
