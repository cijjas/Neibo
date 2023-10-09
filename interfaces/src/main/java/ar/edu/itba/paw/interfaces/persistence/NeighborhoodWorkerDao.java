package ar.edu.itba.paw.interfaces.persistence;

public interface NeighborhoodWorkerDao {
        // --------------------------------------- NIEGHBORHOODWORKERS SELECT ------------------------------------------
        void addWorkerToNeighborhood(long workerId, long neighborhoodId);

        // --------------------------------------- NIEGHBORHOODWORKERS DELETE ------------------------------------------
        void removeWorkerFromNeighborhood(long workerId, long neighborhoodId);
}
