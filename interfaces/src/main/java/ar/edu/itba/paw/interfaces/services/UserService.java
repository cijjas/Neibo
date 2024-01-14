package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.Entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createNeighbor(final String mail, final String password, final String name, final String surname,
                        final long neighborhoodId, final Language language, final String identification);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<User> findUser(final long neighborId);

    Optional<User> findUser(final String mail);

    boolean isAttending(long eventId, long userId);

    List<User> getNeighbors(long neighborhoodId);

    List<User> getNeighborsSubscribed(long postId);

    int countUsers(UserRole role, long neighborhoodId, int page);

    List<User> getUsers(UserRole role, long neighborhoodId, int page, int size);

    int calculateUserPages(UserRole role, long neighborhoodId, int size);

    List<User> getEventUsers(long eventId);

    List<User> getEventUsers(long eventId, int page, int size);

    int calculateEventPages(long eventId, int size);

    List<User> getProductRequesters(long productId, int page, int size);

    // -----------------------------------------------------------------------------------------------------------------

    User updateUser(long userId, String mail, String name, String surname, String password, Boolean darkMode, String phoneNumber, MultipartFile profilePicture, Integer identification, Integer languageId, Integer userRoleId);

}
