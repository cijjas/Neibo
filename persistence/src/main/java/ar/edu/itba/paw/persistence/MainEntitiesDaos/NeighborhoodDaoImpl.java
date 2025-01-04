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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<Neighborhood> findNeighborhood(String name) {
        LOGGER.debug("Selecting Neighborhood with name {}", name);

        TypedQuery<Neighborhood> query = em.createQuery("FROM Neighborhood WHERE neighborhoodname = :neighborhoodName", Neighborhood.class);
        query.setParameter("neighborhoodName", name);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Neighborhood> getNeighborhoods(Long withWorkerId, Long withoutWorkerId, int page, int size) {
        LOGGER.debug("Selecting Neighborhoods with Worker Id {} and without Worker Id {}", withWorkerId, withoutWorkerId);

        // Build the query
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT DISTINCT n.neighborhoodid " +
                        "FROM neighborhoods n " +
                        "LEFT JOIN workers_neighborhoods wn ON n.neighborhoodid = wn.neighborhoodid "
        );

        if (withWorkerId != null) {
            queryBuilder.append("WHERE wn.workerid = :withWorkerId ");
        }
        if (withoutWorkerId != null) {
            if (withWorkerId != null) {
                queryBuilder.append("AND ");
            } else {
                queryBuilder.append("WHERE ");
            }
            queryBuilder.append("n.neighborhoodid NOT IN ( " +
                    "SELECT wn2.neighborhoodid " +
                    "FROM neighborhoods neigh " +
                    "LEFT JOIN workers_neighborhoods wn2 ON neigh.neighborhoodid = wn2.neighborhoodid " +
                    "WHERE wn2.workerid = :withoutWorkerId ) ");
        }

        queryBuilder.append("ORDER BY n.neighborhoodid");

        // Create and parameterize the query
        Query query = em.createNativeQuery(queryBuilder.toString());
        if (withWorkerId != null) {
            query.setParameter("withWorkerId", withWorkerId);
        }
        if (withoutWorkerId != null) {
            query.setParameter("withoutWorkerId", withoutWorkerId);
        }
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        // Execute the query and map results
        List<?> result = query.getResultList();
        List<Long> neighborhoodIds = result.stream()
                .map(id -> ((Number) id).longValue())
                .collect(Collectors.toList());

        if (neighborhoodIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch neighborhoods based on the IDs
        TypedQuery<Neighborhood> dataQuery = em.createQuery(
                "SELECT n FROM Neighborhood n WHERE n.neighborhoodId IN :neighborhoodIds ORDER BY n.neighborhoodId",
                Neighborhood.class
        );
        dataQuery.setParameter("neighborhoodIds", neighborhoodIds);

        return dataQuery.getResultList();
    }

    @Override
    public List<Long> getNeighborhoodIds() {
        LOGGER.debug("Selecting All Neighborhood Ids");

        String jpql = "SELECT n.id FROM Neighborhood n";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        return query.getResultList();
    }

    @Override
    public int countNeighborhoods(Long withWorkerId, Long withoutWorkerId) {
        LOGGER.debug("Counting Neighborhoods with Worker Id {} and without Worker Id {}", withWorkerId, withoutWorkerId);

        // Build the query
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT COUNT(DISTINCT n.neighborhoodid) " +
                        "FROM neighborhoods n " +
                        "LEFT JOIN workers_neighborhoods wn ON n.neighborhoodid = wn.neighborhoodid "
        );

        if (withWorkerId != null) {
            queryBuilder.append("WHERE wn.workerid = :withWorkerId ");
        }
        if (withoutWorkerId != null) {
            if (withWorkerId != null) {
                queryBuilder.append("AND ");
            } else {
                queryBuilder.append("WHERE ");
            }
            queryBuilder.append("n.neighborhoodid NOT IN ( " +
                    "SELECT wn2.neighborhoodid " +
                    "FROM neighborhoods neigh " +
                    "LEFT JOIN workers_neighborhoods wn2 ON neigh.neighborhoodid = wn2.neighborhoodid " +
                    "WHERE wn2.workerid = :withoutWorkerId ) ");
        }

        // Create and parameterize the query
        Query query = em.createNativeQuery(queryBuilder.toString());
        if (withWorkerId != null) {
            query.setParameter("withWorkerId", withWorkerId);
        }
        if (withoutWorkerId != null) {
            query.setParameter("withoutWorkerId", withoutWorkerId);
        }

        // Execute the query and return the count
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
