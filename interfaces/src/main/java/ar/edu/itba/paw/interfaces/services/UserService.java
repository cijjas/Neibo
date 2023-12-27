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

    List<User> getUsersByCriteria(UserRole role, long neighborhoodId, int page, int size);

    int getTotalPages(UserRole role, long neighborhoodId, int size);

    List<User> getEventUsers(long eventId);

    List<User> getEventUsersByCriteria(long eventId, int page, int size);

    int getTotalEventPages(long eventId, int size);

    List<User> getProductRequesters(long productId, int page, int size);

    // -----------------------------------------------------------------------------------------------------------------

    void updateProfilePicture(long userId, MultipartFile image);

    void updatePhoneNumber(long userId, String phoneNumber);

    void toggleDarkMode(final long id);

    void verifyNeighbor(final long id);

    void unverifyNeighbor(final long id, final long neighborhoodId);

    void rejectNeighbor(final long id);

    void toggleLanguage(long id);

    void changeNeighborhood(long userId, long neighborhoodId);
}
