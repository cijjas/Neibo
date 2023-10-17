package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Neighborhood;

import java.util.List;

public interface NeighborhoodWorkerService {
    // --------------------------------------- NIEGHBORHOODWORKERS SELECT ------------------------------------------
    void addWorkerToNeighborhood(long workerId, long neighborhoodId);

    // --------------------------------------- NIEGHBORHOODWORKERS INSERT ------------------------------------------
    List<Neighborhood> getNeighborhoods(long workerId);

    List<Neighborhood> getOtherNeighborhoods(long workerId);

    // --------------------------------------- NIEGHBORHOODWORKERS DELETE ------------------------------------------
    void removeWorkerFromNeighborhood(long workerId, long neighborhoodId);
}
