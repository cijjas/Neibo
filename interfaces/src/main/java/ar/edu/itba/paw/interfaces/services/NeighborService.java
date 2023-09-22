package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Neighbor;

import java.util.List;
import java.util.Optional;

public interface NeighborService {
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

    Neighbor createNeighbor(final String mail, final String password, final String name, final String surname,
                            final long neighborhoodId, String language, boolean darkMode, boolean verified);

    List<Neighbor> getNeighbors();

    List<Neighbor> getNeighborsByNeighborhood(long neighborhoodId);

    Optional<Neighbor> findNeighborByMail(String mail);

    List<Neighbor> getNeighborsSubscribedByPostId(long id);

    Optional<Neighbor> findNeighborById(long id);

    void toggleDarkMode(final long id);

    void verifyNeighbor(final long id);

    void unverifyNeighbor(final long id);

    void updateLanguage(final long id, final String language);

    void setDefaultValues(final long id);

    void setNewPassword(long id, String newPassword);
}
