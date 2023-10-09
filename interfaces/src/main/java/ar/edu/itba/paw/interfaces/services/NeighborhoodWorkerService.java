package ar.edu.itba.paw.interfaces.services;

public interface NeighborhoodWorkerService {
    // --------------------------------------- NIEGHBORHOODWORKERS SELECT ------------------------------------------
    void addWorkerToNeighborhood(long workerId, long neighborhoodId);

    // --------------------------------------- NIEGHBORHOODWORKERS DELETE ------------------------------------------
    void removeWorkerFromNeighborhood(long workerId, long neighborhoodId);
}
