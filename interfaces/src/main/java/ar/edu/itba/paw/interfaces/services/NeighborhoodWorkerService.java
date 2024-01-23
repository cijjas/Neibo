package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Affiliation;

import java.util.List;
import java.util.Set;

public interface NeighborhoodWorkerService {

    void addWorkerToNeighborhood(long workerId, long neighborhoodId);

    void addWorkerToNeighborhoods(long workerId, String neighborhoodIds);

    // -----------------------------------------------------------------------------------------------------------------

    Set<Affiliation> getAffiliations(Long workerId, Long neighborhoodId, int page, int size);

    int countAffiliations(Long workerId, Long neighborhoodId);

    int calculateAffiliationPages(Long workerId, Long neighborhoodId, int size);

    Set<Neighborhood> getNeighborhoods(long workerId);

    List<Neighborhood> getOtherNeighborhoods(long workerId);

    // -----------------------------------------------------------------------------------------------------------------

    void removeWorkerFromNeighborhood(long workerId, long neighborhoodId);

    void verifyWorkerInNeighborhood(long workerId, long neighborhoodId);

    void rejectWorkerFromNeighborhood(long workerId, long neighborhoodId);

    void unverifyWorkerFromNeighborhood(long workerId, long neighborhoodId);

}
