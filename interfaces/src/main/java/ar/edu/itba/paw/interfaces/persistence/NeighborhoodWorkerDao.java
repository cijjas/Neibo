package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Neighborhood;

import java.util.List;

public interface NeighborhoodWorkerDao {

        // --------------------------------------- NEIGHBORHOODS_WORKERS SELECT ----------------------------------------

        void addWorkerToNeighborhood(long workerId, long neighborhoodId);

        // --------------------------------------- NIEGHBORHOODWORKERS SELECT ------------------------------------------

        List<Neighborhood> getNeighborhoods(long workerId);

        // --------------------------------------- NEIGHBORHOODS_WORKERS DELETE ----------------------------------------

        void removeWorkerFromNeighborhood(long workerId, long neighborhoodId);
}
