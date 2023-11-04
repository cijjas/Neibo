package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.models.JunctionEntities.WorkerArea;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;

import java.util.List;

public interface NeighborhoodWorkerDao {

    // --------------------------------------- NEIGHBORHOODS_WORKERS SELECT ----------------------------------------

    WorkerArea createWorkerArea(long workerId, long neighborhoodId);

    // --------------------------------------- NIEGHBORHOODWORKERS SELECT ------------------------------------------

    List<Neighborhood> getNeighborhoods(long workerId);

    // --------------------------------------- NEIGHBORHOODS_WORKERS DELETE ----------------------------------------

    boolean deleteWorkerArea(long workerId, long neighborhoodId);

    public void setNeighborhoodRole(long workerId, WorkerRole role, long neighborhoodId);
}
