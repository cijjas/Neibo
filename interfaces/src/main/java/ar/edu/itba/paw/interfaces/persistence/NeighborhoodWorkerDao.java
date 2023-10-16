package ar.edu.itba.paw.interfaces.persistence;

public interface NeighborhoodWorkerDao {

        // --------------------------------------- NEIGHBORHOODS_WORKERS SELECT ----------------------------------------

        void addWorkerToNeighborhood(long workerId, long neighborhoodId);


        // --------------------------------------- NEIGHBORHOODS_WORKERS DELETE ----------------------------------------

        void removeWorkerFromNeighborhood(long workerId, long neighborhoodId);
}
