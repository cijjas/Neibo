package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.Entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User createNeighbor(final String mail, final String password, final String name, final String surname,
                        final long neighborhoodId, final String language, final String identification);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<User> findUser(final long userId);

    Optional<User> findUser(final long userId, long neighborhoodId);

    Optional<User> findUser(final String mail);

    boolean isAttending(long eventId, long userId);

    List<User> getNeighbors(long neighborhoodId);

    List<User> getNeighborsSubscribed(long postId);

    int countUsers(String userRoleURN, long neighborhoodId);

    List<User> getUsers(String userRoleURN, long neighborhoodId, int page, int size);

    int calculateUserPages(String userRoleURN, long neighborhoodId, int size);

    List<User> getEventUsers(long eventId);

    List<User> getEventUsers(long eventId, int page, int size);

    int calculateEventPages(long eventId, int size);

    List<User> getProductRequesters(long productId, int page, int size);

    // -----------------------------------------------------------------------------------------------------------------

    User updateUser(long userId, String mail, String name, String surname, String password, Boolean darkMode, String phoneNumber, String profilePictureURN, Integer identification, String languageURN, String userRoleURN);

}
