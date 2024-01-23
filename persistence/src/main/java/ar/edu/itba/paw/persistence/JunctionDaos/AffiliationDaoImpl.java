package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.enums.WorkerRole;
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
    public Affiliation createAffiliation(long workerId, long neighborhoodId) {
        LOGGER.debug("Inserting Worker {} to Neighborhood {}", workerId, neighborhoodId);

        Affiliation affiliation = new Affiliation(em.find(Worker.class, workerId), em.find(Neighborhood.class, neighborhoodId));
        affiliation.setRole(WorkerRole.UNVERIFIED_WORKER);
        em.persist(affiliation);
        return affiliation;
    }

    @Override
    public Set<Affiliation> getAffiliations(Long workerId, Long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Worker Affiliations By Criteria");

        TypedQuery<AffiliationKey> idQuery = null;
        if (workerId != null && neighborhoodId != null) {
            idQuery = em.createQuery(
                    "SELECT a.id FROM Affiliation a WHERE a.worker.id = :workerId AND a.neighborhood.neighborhoodId = :neighborhoodId", AffiliationKey.class);
            idQuery.setParameter("workerId", workerId);
            idQuery.setParameter("neighborhoodId", neighborhoodId);
        } else if (workerId != null) {
            idQuery = em.createQuery(
                    "SELECT a.id FROM Affiliation a WHERE a.worker.id = :workerId", AffiliationKey.class);
            idQuery.setParameter("workerId", workerId);
        } else if (neighborhoodId != null) {
            idQuery = em.createQuery(
                    "SELECT a.id FROM Affiliation a WHERE a.neighborhood.neighborhoodId = :neighborhoodId", AffiliationKey.class);
            idQuery.setParameter("neighborhoodId", neighborhoodId);
        } else {
            idQuery = em.createQuery(
                    "SELECT a.id FROM Affiliation a", AffiliationKey.class);
        }

        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);
        List<AffiliationKey> ids = idQuery.getResultList();

        if (!ids.isEmpty()) {
            TypedQuery<Affiliation> affiliationQuery = em.createQuery(
                    "SELECT a FROM Affiliation a WHERE a.id IN :ids", Affiliation.class);
            affiliationQuery.setParameter("ids", ids);
            return new HashSet<>(affiliationQuery.getResultList());
        }
        return Collections.emptySet();
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

        Affiliation affiliation = em.find(Affiliation.class, new AffiliationKey(workerId, neighborhoodId));
        if (affiliation != null) {
            em.remove(affiliation);
            return true;
        } else {
            return false;
        }
    }
}
