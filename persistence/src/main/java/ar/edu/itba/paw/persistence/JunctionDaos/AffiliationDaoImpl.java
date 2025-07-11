package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.interfaces.persistence.AffiliationDao;
import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.models.compositeKeys.AffiliationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class AffiliationDaoImpl implements AffiliationDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AffiliationDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------- NEIGHBORHOOD WORKERS INSERT -------------------------------------------

    @Override
    public Affiliation createAffiliation(long neighborhoodId, long workerId, long workerRoleId) {
        LOGGER.debug("Inserting Affiliation between Worker Id {} and Neighborhood Id {}", workerId, neighborhoodId);

        Worker worker = em.find(Worker.class, workerId);
        Neighborhood neighborhood = em.find(Neighborhood.class, neighborhoodId);
        WorkerRole workerRole = WorkerRole.fromId(workerRoleId).get(); // Controller layer guarantees non-empty Optional

        Affiliation affiliation = new Affiliation(worker, neighborhood, workerRole, new Date(System.currentTimeMillis()));

        em.persist(affiliation);
        return affiliation;
    }

    // ----------------------------------------- NEIGHBORHOOD WORKERS SELECT -------------------------------------------

    @Override
    public Optional<Affiliation> findAffiliation(long neighborhoodId, long workerId) {
        LOGGER.debug("Finding Affiliation with Worker Id {} in Neighborhood Id {}", workerId, neighborhoodId);

        return Optional.ofNullable(em.find(Affiliation.class, new AffiliationKey(workerId, neighborhoodId)));
    }

    @Override
    public List<Affiliation> getAffiliations(Long neighborhoodId, Long workerId, int page, int size) {
        LOGGER.debug("Selecting Worker Affiliations with Worker Id {} and with Neighborhood Id {}", workerId, neighborhoodId);

        StringBuilder queryBuilder = new StringBuilder("SELECT a.id FROM Affiliation a ");

        if (workerId != null && neighborhoodId != null) {
            queryBuilder.append("WHERE a.worker.id = :workerId AND a.neighborhood.neighborhoodId = :neighborhoodId ");
        } else if (workerId != null) {
            queryBuilder.append("WHERE a.worker.id = :workerId ");
        } else if (neighborhoodId != null) {
            queryBuilder.append("WHERE a.neighborhood.neighborhoodId = :neighborhoodId ");
        }

        queryBuilder.append("ORDER BY a.requestDate ASC");

        TypedQuery<AffiliationKey> idQuery = em.createQuery(queryBuilder.toString(), AffiliationKey.class);

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
                    "SELECT a FROM Affiliation a WHERE a.id IN :ids ORDER BY a.requestDate ASC", Affiliation.class);
            affiliationQuery.setParameter("ids", ids);
            return affiliationQuery.getResultList();
        }

        return Collections.emptyList();
    }

    @Override
    public int countAffiliations(Long neighborhoodId, Long workerId) {
        LOGGER.debug("Counting Worker Affiliations with Worker Id {} and with Neighborhood Id {}", workerId, neighborhoodId);

        Long count;
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
            count = (Long) em.createQuery("SELECT COUNT(a) FROM Affiliation a").getSingleResult();
        }

        return count != null ? count.intValue() : 0;
    }

    // ----------------------------------------- NEIGHBORHOOD WORKERS DELETE -------------------------------------------

    @Override
    public boolean deleteAffiliation(long neighborhoodId, long workerId) {
        LOGGER.debug("Deleting Affiliation with Worker Id {} and Neighborhood Id {}", workerId, neighborhoodId);

        String hql = "DELETE FROM Affiliation a WHERE a.id = :affiliationId";
        int deletedCount = em.createQuery(hql)
                .setParameter("affiliationId", new AffiliationKey(workerId, neighborhoodId))
                .executeUpdate();
        return deletedCount > 0;
    }
}
