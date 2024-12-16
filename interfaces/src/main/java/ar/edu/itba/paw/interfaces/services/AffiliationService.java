package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Affiliation;

import java.util.List;

public interface AffiliationService {

    Affiliation createAffiliation(long neighborhoodId, long workerId, long workerRoleId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Affiliation> getAffiliations(Long neighborhoodId, Long workerId, int page, int size);

    int calculateAffiliationPages(Long neighborhoodId, Long workerId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Affiliation updateAffiliation(long neighborhoodId, long workerId, Long workerRoleId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAffiliation(long neighborhoodId, long workerId);
}
