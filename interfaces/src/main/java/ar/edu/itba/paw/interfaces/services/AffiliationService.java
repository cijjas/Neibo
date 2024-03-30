package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Affiliation;

import java.util.List;
import java.util.Set;

public interface AffiliationService {

    Set<Affiliation> getAffiliations(Long workerId, Long neighborhoodId, int page, int size);

    int countAffiliations(Long workerId, Long neighborhoodId);

    int calculateAffiliationPages(Long workerId, Long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Affiliation createAffiliation(String workerURN, String neighborhoodURNs, String workerStatus);

    // -----------------------------------------------------------------------------------------------------------------

    Affiliation updateAffiliation(String workerURN, String neighborhoodURNs, String workerRole);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteAffiliation(String workerURN, String neighborhoodURNs);

    // -----------------------------------------------------------------------------------------------------------------

    // deprecate this shit

    Set<Neighborhood> getNeighborhoods(long workerId);

    List<Neighborhood> getOtherNeighborhoods(long workerId);

}
