package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Affiliation;

import java.util.List;

public interface AffiliationService {

    Affiliation createAffiliation(long workerId, long neighborhoodId, long workerRoleId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Affiliation> getAffiliations(Long workerId, Long neighborhoodId, int page, int size);

    int calculateAffiliationPages(Long workerId, Long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Affiliation updateAffiliation(long workerId, long neighborhoodId, Long workerRoleId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAffiliation(long workerId, long neighborhoodId);
}
