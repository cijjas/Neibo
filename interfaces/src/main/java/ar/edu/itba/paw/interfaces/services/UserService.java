package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(final long neighborhoodId, final String mail, final String name, final String surname, final String password,
                    final Integer identification, final Long languageId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<User> findUser(long neighborhoodId, final long userId);

    Optional<User> findUser(final String mail);

    List<User> getUsers(long neighborhoodId, Long userRoleId, int page, int size);

    int calculateUserPages(long neighborhoodId, Long userRoleId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    User updateUser(long userId, String mail, String name, String surname, String password, Integer identification, Long languageId,
                    Long profilePictureId, Boolean darkMode, String phoneNumber, Long userRoleId);
}
