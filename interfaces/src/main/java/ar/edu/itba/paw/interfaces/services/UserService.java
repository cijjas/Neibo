package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.User;
import enums.Language;

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

    List<User> getUsers();

    Optional<User> findUserById(final long neighborId);

    public Optional<User> findUserByMail(final String mail);

    User createNeighbor(final String mail, final String password, final String name, final String surname,
                        final long neighborhoodId, Language language);

    List<User> getNeighbors();

    List<User> getNeighborsByNeighborhood(long neighborhoodId);

    Optional<User> findNeighborByMail(String mail);

    List<User> getNeighborsSubscribedByPostId(long id);

    List<User> getUnverifiedNeighborsByNeighborhood(long neighborhoodId);

    Optional<User> findNeighborById(long id);

    void toggleDarkMode(final long id);

    void verifyNeighbor(final long id);

    void unverifyNeighbor(final long id);

    void updateLanguage(final long id, final Language language);

    void resetPreferenceValues(final long id);

    void setNewPassword(long id, String newPassword);
}
