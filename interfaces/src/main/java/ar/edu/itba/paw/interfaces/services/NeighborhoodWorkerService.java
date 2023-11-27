package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Neighborhood;

import java.util.List;

public interface NeighborhoodWorkerService {

    void addWorkerToNeighborhood(long workerId, long neighborhoodId);

    void addWorkerToNeighborhoods(long workerId, String neighborhoodIds);

    // -----------------------------------------------------------------------------------------------------------------

    List<Neighborhood> getNeighborhoods(long workerId);

    List<Neighborhood> getOtherNeighborhoods(long workerId);

    // -----------------------------------------------------------------------------------------------------------------

    void removeWorkerFromNeighborhood(long workerId, long neighborhoodId);

    void verifyWorkerInNeighborhood(long workerId, long neighborhoodId);

    void rejectWorkerFromNeighborhood(long workerId, long neighborhoodId);

    void unverifyWorkerFromNeighborhood(long workerId, long neighborhoodId);

}
