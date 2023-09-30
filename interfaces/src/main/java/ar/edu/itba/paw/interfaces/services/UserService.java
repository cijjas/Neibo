package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.User;
import enums.Language;
import enums.UserRole;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    /*
        Use descriptive names that indicate the purpose of the method.
        Prefix service layer methods with verbs like "create," "retrieve," "update," or "delete" to indicate their primary action.
        Use camelCase or PascalCase for method names, depending on your project's convention.
        Include domain-specific terms when applicable.
        Examples:
            createUserAccount
            getUserProfile
            updateProductDetails
            deleteOrder
     */

    // -----------------------------------------------------------------------------------------------------------------

    User createNeighbor(final String mail, final String password, final String name, final String surname,
                        final long neighborhoodId, Language language);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<User> findUserById(final long neighborId);

    Optional<User> findUserByMail(final String mail);

    List<User> getNeighbors(long neighborhoodId);

    List<User> getNeighborsSubscribedByPostId(long id);

    List<User> getUsersPage(UserRole role, long neighborhoodId, int page, int size);

    int getTotalPages(UserRole role, long neighborhoodId, int size );


    // -----------------------------------------------------------------------------------------------------------------

    void storeProfilePicture(long userId, MultipartFile image);

    void toggleDarkMode(final long id);

    void verifyNeighbor(final long id);

    void unverifyNeighbor(final long id);

    public void toggleLanguage(long id);

    void updateLanguage(final long id, final Language language);

    void resetPreferenceValues(final long id);

    void setNewPassword(long id, String newPassword);
}
