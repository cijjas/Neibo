package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Affiliation;

import java.util.List;
import java.util.Optional;

public interface AffiliationDao {

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ----------------------------------------

    Affiliation createAffiliation(long workerId, long neighborhoodId, Long workerRoleId);

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ------------------------------------------

    Optional<Affiliation> findAffiliation(long workerId, long neighborhoodId);

    List<Affiliation> getAffiliations(Long workerId, Long neighborhoodId, int page, int size);

    int countAffiliations(Long workerId, Long neighborhoodId);

    // --------------------------------------- NEIGHBORHOODS WORKERS DELETE ----------------------------------------

    boolean deleteAffiliation(long workerId, long neighborhoodId);
}
