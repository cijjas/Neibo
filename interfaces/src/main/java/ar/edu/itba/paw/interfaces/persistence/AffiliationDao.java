package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Affiliation;

import java.util.Optional;
import java.util.Set;

public interface AffiliationDao {

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ----------------------------------------

    Affiliation createAffiliation(long workerId, long neighborhoodId);

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ------------------------------------------

    Set<Affiliation> getAffiliations(Long workerId, Long neighborhoodId, int page, int size);

    int countAffiliations(Long workerId, Long neighborhoodId);

    Optional<Affiliation> findAffiliation(long workerId, long neighborhoodId);

    Set<Neighborhood> getNeighborhoods(long workerId);

    // --------------------------------------- NEIGHBORHOODS WORKERS DELETE ----------------------------------------

    boolean deleteAffiliation(long workerId, long neighborhoodId);
}
