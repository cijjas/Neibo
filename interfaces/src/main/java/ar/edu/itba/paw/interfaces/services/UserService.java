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

    Optional<User> findUserById(final long neighborId);

    Optional<User> findUserByMail(final String mail);

    boolean isAttending(long eventId, long userId);

    List<User> getNeighbors(long neighborhoodId);

    List<User> getNeighborsSubscribedByPostId(long id);

    int countUsers(UserRole role, long neighborhoodId, int page);

    List<User> getUsersByCriteria(UserRole role, long neighborhoodId, int page, int size);

    int calculateUserPagesByCriteria(UserRole role, long neighborhoodId, int size);

    List<User> getEventUsers(long eventId);

    List<User> getEventUsersByCriteria(long eventId, int page, int size);

    int calculateEventPagesByCriteria(long eventId, int size);

    List<User> getProductRequesters(long productId, int page, int size);

    // -----------------------------------------------------------------------------------------------------------------

    User updateUser(long id, String mail, String name, String surname, String password, Boolean darkMode, String phoneNumber, MultipartFile profilePicture, Integer identification, Integer languageId, Integer userRoleId);

}
