package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Affiliation;

import java.util.List;

public interface AffiliationService {

    Affiliation createAffiliation(String workerURN, String neighborhoodURNs, String workerRoleURN);

    // -----------------------------------------------------------------------------------------------------------------

    List<Affiliation> getAffiliations(String workerURN, String neighborhoodURN, int page, int size);

    int calculateAffiliationPages(String workerURN, String neighborhoodURN, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Affiliation updateAffiliation(String workerURN, String neighborhoodURNs, String workerRole);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAffiliation(String workerURN, String neighborhoodURNs);
}
