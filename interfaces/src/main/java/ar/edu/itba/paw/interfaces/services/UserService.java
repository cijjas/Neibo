package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(long neighborhoodId, String mail, String name, String surname, String password, Integer identification, Long languageId, Long userRoleId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<User> findUser(long userId);

    Optional<User> findUser(String mail);

    List<User> getUsers(Long neighborhoodId, Long userRoleId, int page, int size);

    int countUsers(Long neighborhoodId, Long userRoleId);

    // -----------------------------------------------------------------------------------------------------------------

    User updateUser(long userId, Long neighborhoodId, String mail, String name, String surname, String password, Integer identification, Long languageId,
                    Long profilePictureId, Boolean darkMode, String phoneNumber, Long userRoleId);
}
