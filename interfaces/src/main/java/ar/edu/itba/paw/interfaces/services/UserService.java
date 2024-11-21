package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createNeighbor(final String mail, final String password, final String name, final String surname,
                        final long neighborhoodId, final Long languageId, final Integer identification);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<User> findUser(final long userId);

    Optional<User> findUser(final long userId, long neighborhoodId);

    Optional<User> findUser(final String mail);

    List<User> getUsers(Long userRoleId, long neighborhoodId, int page, int size);

    // ---------------------------------------------------

    int calculateUserPages(Long userRoleId, long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    User updateUser(long userId, String mail, String name, String surname, String password, Boolean darkMode, String phoneNumber, Long profilePictureId, Integer identification, Long languageId, Long userRoleId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteUser(long userId);
}
