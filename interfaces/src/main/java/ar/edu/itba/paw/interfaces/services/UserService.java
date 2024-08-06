package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createNeighbor(final String mail, final String password, final String name, final String surname,
                        final long neighborhoodId, final String language, final String identification);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<User> findUser(final long userId);

    Optional<User> findUser(final long userId, long neighborhoodId);

    Optional<User> findUser(final String mail);

    List<User> getNeighbors(long neighborhoodId, int page, int size);

    List<User> getNeighborsSubscribed(long postId);

    List<User> getUsers(String userRoleURN, long neighborhoodId, int page, int size);

    // ---------------------------------------------------

    int calculateUserPages(String userRoleURN, long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    User updateUser(long userId, String mail, String name, String surname, String password, Boolean darkMode, String phoneNumber, String profilePictureURN, Integer identification, String languageURN, String userRoleURN);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteUser(long userId);
}
