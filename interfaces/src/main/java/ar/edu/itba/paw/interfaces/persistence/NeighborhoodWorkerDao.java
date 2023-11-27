package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.WorkerArea;
import ar.edu.itba.paw.models.Entities.Neighborhood;

import java.util.List;
import java.util.Optional;

public interface NeighborhoodWorkerDao {

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ----------------------------------------

    WorkerArea createWorkerArea(long workerId, long neighborhoodId);

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ------------------------------------------

    Optional<WorkerArea> findWorkerArea(long workerId, long neighborhoodId);

    List<Neighborhood> getNeighborhoods(long workerId);

    // --------------------------------------- NEIGHBORHOODS WORKERS DELETE ----------------------------------------

    boolean deleteWorkerArea(long workerId, long neighborhoodId);
}
