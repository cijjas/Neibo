package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.models.JunctionEntities.WorkerArea;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;

import java.util.List;

public interface NeighborhoodWorkerDao {

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ----------------------------------------

    WorkerArea createWorkerArea(long workerId, long neighborhoodId);

    // --------------------------------------- NEIGHBORHOODS WORKERS SELECT ------------------------------------------

    List<Neighborhood> getNeighborhoods(long workerId);

    // --------------------------------------- NEIGHBORHOODS WORKERS DELETE ----------------------------------------

    boolean deleteWorkerArea(long workerId, long neighborhoodId);

    void setNeighborhoodRole(long workerId, WorkerRole role, long neighborhoodId);
}
