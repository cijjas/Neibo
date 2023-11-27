package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.models.compositeKeys.WorkerAreaKey;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.models.Entities.WorkerArea;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class NeighborhoodWorkerDaoImpl implements NeighborhoodWorkerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodWorkerDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------- NEIGHBORHOOD WORKERS INSERT -------------------------------------------

    @Override
    public WorkerArea createWorkerArea(long workerId, long neighborhoodId) {
        LOGGER.debug("Inserting Worker {} to Neighborhood {}", workerId, neighborhoodId);
        WorkerArea workerArea = new WorkerArea(em.find(Worker.class, workerId), em.find(Neighborhood.class, neighborhoodId));
        em.persist(workerArea);
        return workerArea;
    }

    // ----------------------------------------- NEIGHBORHOOD WORKERS SELECT -------------------------------------------

    @Override
    public Optional<WorkerArea> findWorkerArea(long workerId, long neighborhoodId) {
        LOGGER.debug("Finding Worker area with worker id {} in Neighborhood {}", workerId, neighborhoodId);
        return Optional.ofNullable(em.find(WorkerArea.class, new WorkerAreaKey(workerId, neighborhoodId)));
    }

    @Override
    public List<Neighborhood> getNeighborhoods(long workerId) {
        LOGGER.debug("Selecting neighborhoods for the Worker {}", workerId);
        TypedQuery<Neighborhood> query = em.createQuery("SELECT n FROM Worker w JOIN w.workNeighborhoods n WHERE w.user.id = :workerId", Neighborhood.class);
        query.setParameter("workerId", workerId);
        return query.getResultList();
    }

    // ----------------------------------------- NEIGHBORHOOD WORKERS DELETE -------------------------------------------

    @Override
    public boolean deleteWorkerArea(long workerId, long neighborhoodId) {
        LOGGER.debug("Deleting Worker {} from Neighborhood {}", workerId, neighborhoodId);
        WorkerArea workerArea = em.find(WorkerArea.class, new WorkerAreaKey(workerId, neighborhoodId));
        if (workerArea != null) {
            em.remove(workerArea);
            return true;
        } else {
            return false;
        }
    }
}
