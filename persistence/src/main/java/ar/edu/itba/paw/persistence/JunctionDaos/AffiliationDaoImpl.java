package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AffiliationDao;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.models.compositeKeys.AffiliationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

@Repository
public class AffiliationDaoImpl implements AffiliationDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AffiliationDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------- NEIGHBORHOOD WORKERS INSERT -------------------------------------------

    @Override
    public Affiliation createAffiliation(long workerId, long neighborhoodId, Long workerRoleId) {
        LOGGER.debug("Inserting Worker {} to Neighborhood {}", workerId, neighborhoodId);

        Worker worker = em.find(Worker.class, workerId);
        Neighborhood neighborhood = em.find(Neighborhood.class, neighborhoodId);
        WorkerRole workerRole = WorkerRole.fromId(workerRoleId);

        Affiliation affiliation;
        if (workerRole == null )
            affiliation = new Affiliation(worker, neighborhood, WorkerRole.UNVERIFIED_WORKER);
        else
            affiliation = new Affiliation(worker, neighborhood, workerRole);

        em.persist(affiliation);
        return affiliation;
    }

    @Override
    public List<Affiliation> getAffiliations(Long workerId, Long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Worker Affiliations By Criteria");

        TypedQuery<AffiliationKey> idQuery = null;
        StringBuilder queryBuilder = new StringBuilder("SELECT a.id FROM Affiliation a ");

        boolean whereClauseAdded = false;
        if (workerId != null && neighborhoodId != null) {
            queryBuilder.append("WHERE a.worker.id = :workerId AND a.neighborhood.neighborhoodId = :neighborhoodId ");
            whereClauseAdded = true;
        } else if (workerId != null) {
            queryBuilder.append("WHERE a.worker.id = :workerId ");
            whereClauseAdded = true;
        } else if (neighborhoodId != null) {
            queryBuilder.append("WHERE a.neighborhood.neighborhoodId = :neighborhoodId ");
            whereClauseAdded = true;
        }

        queryBuilder.append("ORDER BY a.worker.id, a.neighborhood.neighborhoodId");

        idQuery = em.createQuery(queryBuilder.toString(), AffiliationKey.class);

        if (workerId != null) {
            idQuery.setParameter("workerId", workerId);
        }
        if (neighborhoodId != null) {
            idQuery.setParameter("neighborhoodId", neighborhoodId);
        }

        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<AffiliationKey> ids = idQuery.getResultList();

        if (!ids.isEmpty()) {
            TypedQuery<Affiliation> affiliationQuery = em.createQuery(
                    "SELECT a FROM Affiliation a WHERE a.id IN :ids ORDER BY a.worker.id, a.neighborhood.neighborhoodId", Affiliation.class);
            affiliationQuery.setParameter("ids", ids);
            return affiliationQuery.getResultList();
        }
        return Collections.emptyList();
    }

    @Override
    public int countAffiliations(Long workerId, Long neighborhoodId) {
        LOGGER.debug("Counting Worker Affiliations By Criteria");

        Long count = null;
        if (workerId != null && neighborhoodId != null) {
            count = (Long) em.createQuery(
                            "SELECT COUNT(a) FROM Affiliation a WHERE a.worker.id = :workerId AND a.neighborhood.neighborhoodId = :neighborhoodId")
                    .setParameter("workerId", workerId)
                    .setParameter("neighborhoodId", neighborhoodId)
                    .getSingleResult();
        } else if (workerId != null) {
            count = (Long) em.createQuery(
                            "SELECT COUNT(a) FROM Affiliation a WHERE a.worker.id = :workerId")
                    .setParameter("workerId", workerId)
                    .getSingleResult();
        } else if (neighborhoodId != null) {
            count = (Long) em.createQuery(
                            "SELECT COUNT(a) FROM Affiliation a WHERE a.neighborhood.neighborhoodId = :neighborhoodId")
                    .setParameter("neighborhoodId", neighborhoodId)
                    .getSingleResult();
        } else {
            count = (Long) em.createQuery(
                            "SELECT COUNT(a) FROM Affiliation a")
                    .getSingleResult();
        }

        return count != null ? count.intValue() : 0;
    }


    // ----------------------------------------- NEIGHBORHOOD WORKERS SELECT -------------------------------------------

    @Override
    public Optional<Affiliation> findAffiliation(long workerId, long neighborhoodId) {
        LOGGER.debug("Finding Affiliation with worker id {} in Neighborhood {}", workerId, neighborhoodId);

        return Optional.ofNullable(em.find(Affiliation.class, new AffiliationKey(workerId, neighborhoodId)));
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
    public boolean deleteAffiliation(long workerId, long neighborhoodId) {
        LOGGER.debug("Deleting Worker {} from Neighborhood {}", workerId, neighborhoodId);

        String hql = "DELETE FROM Affiliation a WHERE a.id = :affiliationId";
        int deletedCount = em.createQuery(hql)
                .setParameter("affiliationId", new AffiliationKey(workerId, neighborhoodId))
                .executeUpdate();
        return deletedCount > 0;
    }
}
