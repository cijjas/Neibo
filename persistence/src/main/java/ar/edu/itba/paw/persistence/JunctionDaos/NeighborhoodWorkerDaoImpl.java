package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.models.Entities.WorkerArea;
import ar.edu.itba.paw.models.compositeKeys.WorkerAreaKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

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
        workerArea.setRole(WorkerRole.UNVERIFIED_WORKER);
        em.persist(workerArea);
        return workerArea;
    }

    @Override
    public Set<WorkerArea> getAffiliations(Long workerId, Long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Worker Affiliations By Criteria");

        TypedQuery<WorkerAreaKey> idQuery = null;
        if (workerId != null && neighborhoodId != null) {
            idQuery = em.createQuery(
                    "SELECT wa.id FROM WorkerArea wa WHERE wa.id.workerId = :workerId AND wa.id.neighborhoodId = :neighborhoodId", WorkerAreaKey.class);
            idQuery.setParameter("workerId", workerId);
            idQuery.setParameter("neighborhoodId", neighborhoodId);
        } else if (workerId != null) {
            idQuery = em.createQuery(
                    "SELECT wa.id FROM WorkerArea wa WHERE wa.id.workerId = :workerId", WorkerAreaKey.class);
            idQuery.setParameter("workerId", workerId);
        } else if (neighborhoodId != null) {
            idQuery = em.createQuery(
                    "SELECT wa.id FROM WorkerArea wa WHERE wa.id.neighborhoodId = :neighborhoodId", WorkerAreaKey.class);
            idQuery.setParameter("neighborhoodId", neighborhoodId);
        } else {
            idQuery = em.createQuery(
                    "SELECT wa.id FROM WorkerArea wa", WorkerAreaKey.class);
        }

        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);
        List<WorkerAreaKey> ids = idQuery.getResultList();

        if (!ids.isEmpty()) {
            TypedQuery<WorkerArea> workerAreaQuery = em.createQuery(
                    "SELECT wa FROM WorkerArea wa WHERE wa.id IN :ids", WorkerArea.class);
            workerAreaQuery.setParameter("ids", ids);
            return new HashSet<>(workerAreaQuery.getResultList());
        }
        return Collections.emptySet();
    }

    @Override
    public int countAffiliations(Long workerId, Long neighborhoodId) {
        LOGGER.debug("Counting Worker Affiliations By Criteria");

        Long count = null;
        if (workerId != null && neighborhoodId != null) {
            count = (Long) em.createQuery(
                            "SELECT COUNT(wa) FROM WorkerArea wa WHERE wa.id.workerId = :workerId AND wa.id.neighborhoodId = :neighborhoodId")
                    .setParameter("workerId", workerId)
                    .setParameter("neighborhoodId", neighborhoodId)
                    .getSingleResult();
        } else if (workerId != null) {
            count = (Long) em.createQuery(
                            "SELECT COUNT(wa) FROM WorkerArea wa WHERE wa.id.workerId = :workerId")
                    .setParameter("workerId", workerId)
                    .getSingleResult();
        } else if (neighborhoodId != null) {
            count = (Long) em.createQuery(
                            "SELECT COUNT(wa) FROM WorkerArea wa WHERE wa.id.neighborhoodId = :neighborhoodId")
                    .setParameter("neighborhoodId", neighborhoodId)
                    .getSingleResult();
        } else {
            count = (Long) em.createQuery(
                            "SELECT COUNT(wa) FROM WorkerArea wa")
                    .getSingleResult();
        }

        return count != null ? count.intValue() : 0;
    }


    // ----------------------------------------- NEIGHBORHOOD WORKERS SELECT -------------------------------------------

    @Override
    public Optional<WorkerArea> findWorkerArea(long workerId, long neighborhoodId) {
        LOGGER.debug("Finding Worker area with worker id {} in Neighborhood {}", workerId, neighborhoodId);

        return Optional.ofNullable(em.find(WorkerArea.class, new WorkerAreaKey(workerId, neighborhoodId)));
    }

    @Override
    public Set<Neighborhood> getNeighborhoods(long workerId) {
        LOGGER.debug("Selecting neighborhoods for the Worker {}", workerId);

        TypedQuery<Neighborhood> query = em.createQuery("SELECT n FROM Worker w JOIN w.workNeighborhoods n WHERE w.user.id = :workerId", Neighborhood.class);
        query.setParameter("workerId", workerId);
        return new HashSet<>(query.getResultList());
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
