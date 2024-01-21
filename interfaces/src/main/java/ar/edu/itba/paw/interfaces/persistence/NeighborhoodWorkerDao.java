package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.WorkerArea;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NeighborhoodWorkerDao {

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ----------------------------------------

    WorkerArea createWorkerArea(long workerId, long neighborhoodId);

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ------------------------------------------

    Optional<WorkerArea> findWorkerArea(long workerId, long neighborhoodId);

    Set<Neighborhood> getNeighborhoods(long workerId);

    // --------------------------------------- NEIGHBORHOODS WORKERS DELETE ----------------------------------------

    boolean deleteWorkerArea(long workerId, long neighborhoodId);
}
