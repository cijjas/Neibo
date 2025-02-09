package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class NeighborhoodDaoImpl implements NeighborhoodDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------- NEIGHBORHOODS INSERT --------------------------------------------------

    @Override
    public Neighborhood createNeighborhood(String name) {
        LOGGER.debug("Inserting Neighborhood {}", name);

        Neighborhood neighborhood = new Neighborhood.Builder()
                .name(name)
                .isBase(false)
                .build();
        em.persist(neighborhood);
        return neighborhood;
    }

    // ----------------------------------------- NEIGHBORHOODS SELECT --------------------------------------------------

    @Override
    public Optional<Neighborhood> findNeighborhood(long neighborhoodId) {
        LOGGER.debug("Selecting Neighborhood with Neighborhood Id{}", neighborhoodId);

        return Optional.ofNullable(em.find(Neighborhood.class, neighborhoodId));
    }

    @Override
    public List<Neighborhood> getNeighborhoods(Boolean isBase, Long withWorkerId, Long withoutWorkerId, int page, int size) {
        LOGGER.debug("Selecting Neighborhoods with Worker Id {}, without Worker Id {}, and isBase {}", withWorkerId, withoutWorkerId, isBase);

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT DISTINCT n.* " +
                        "FROM neighborhoods n " +
                        "LEFT JOIN workers_neighborhoods wn ON n.neighborhoodid = wn.neighborhoodid " +
                        "WHERE 1=1 "
        );

        if (isBase != null) {
            queryBuilder.append("AND n.isbase = :isBase ");
        }
        if (withWorkerId != null) {
            queryBuilder.append("AND wn.workerid = :withWorkerId ");
        }
        if (withoutWorkerId != null) {
            queryBuilder.append("AND n.neighborhoodid NOT IN ( " +
                    "SELECT wn2.neighborhoodid " +
                    "FROM neighborhoods neigh " +
                    "LEFT JOIN workers_neighborhoods wn2 ON neigh.neighborhoodid = wn2.neighborhoodid " +
                    "WHERE wn2.workerid = :withoutWorkerId ) ");
        }

        queryBuilder.append("ORDER BY n.neighborhoodid");

        Query query = em.createNativeQuery(queryBuilder.toString(), Neighborhood.class);
        if (isBase != null) {
            query.setParameter("isBase", isBase);
        }
        if (withWorkerId != null) {
            query.setParameter("withWorkerId", withWorkerId);
        }
        if (withoutWorkerId != null) {
            query.setParameter("withoutWorkerId", withoutWorkerId);
        }
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public int countNeighborhoods(Boolean isBase, Long withWorkerId, Long withoutWorkerId) {
        LOGGER.debug("Counting Neighborhoods with Worker Id {}, without Worker Id {}, and isBase {}", withWorkerId, withoutWorkerId, isBase);

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT COUNT(DISTINCT n.neighborhoodid) " +
                        "FROM neighborhoods n " +
                        "LEFT JOIN workers_neighborhoods wn ON n.neighborhoodid = wn.neighborhoodid " +
                        "WHERE 1=1 "
        );

        if (isBase != null) {
            queryBuilder.append("AND n.isbase = :isBase ");
        }
        if (withWorkerId != null) {
            queryBuilder.append("AND wn.workerid = :withWorkerId ");
        }
        if (withoutWorkerId != null) {
            queryBuilder.append("AND n.neighborhoodid NOT IN ( " +
                    "SELECT wn2.neighborhoodid " +
                    "FROM neighborhoods neigh " +
                    "LEFT JOIN workers_neighborhoods wn2 ON neigh.neighborhoodid = wn2.neighborhoodid " +
                    "WHERE wn2.workerid = :withoutWorkerId ) ");
        }
        Query query = em.createNativeQuery(queryBuilder.toString());
        if (isBase != null) {
            query.setParameter("isBase", isBase);
        }
        if (withWorkerId != null) {
            query.setParameter("withWorkerId", withWorkerId);
        }
        if (withoutWorkerId != null) {
            query.setParameter("withoutWorkerId", withoutWorkerId);
        }

        Object result = query.getSingleResult();
        return ((Number) result).intValue();
    }

    // ----------------------------------------- NEIGHBORHOODS DELETE --------------------------------------------------

    @Override
    public boolean deleteNeighborhood(long neighborhoodId) {
        LOGGER.debug("Deleting Neighborhood with Neighborhood Id {}", neighborhoodId);
        Neighborhood neighborhood = em.find(Neighborhood.class, neighborhoodId);
        if (neighborhood != null) {
            em.remove(neighborhood);
            return true;
        }
        return false;
    }
}
