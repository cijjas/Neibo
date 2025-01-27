package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Affiliation;

import java.util.List;
import java.util.Optional;

public interface AffiliationDao {

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ----------------------------------------

    Affiliation createAffiliation(long neighborhoodId, long workerId, long workerRoleId);

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ------------------------------------------

    Optional<Affiliation> findAffiliation(long neighborhoodId, long workerId);

    List<Affiliation> getAffiliations(Long neighborhoodId, Long workerId, int page, int size);

    int countAffiliations(Long neighborhoodId, Long workerId);

    // --------------------------------------- NEIGHBORHOODS WORKERS DELETE ----------------------------------------

    boolean deleteAffiliation(long neighborhoodId, long workerId);
}
