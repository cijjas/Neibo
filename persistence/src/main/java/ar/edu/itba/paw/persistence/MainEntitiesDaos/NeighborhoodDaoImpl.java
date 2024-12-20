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
    public List<Neighborhood> getNeighborhoods(Long workerId, int page, int size) {
        LOGGER.debug("Selecting Neighborhoods with Worker Id {}", workerId);

        // Build the first query to fetch neighborhood IDs with ordering
        StringBuilder idQueryStringBuilder = new StringBuilder();
        idQueryStringBuilder.append("SELECT DISTINCT n.neighborhoodid FROM neighborhoods n ");

        if (workerId != null) {
            idQueryStringBuilder.append("JOIN workers_neighborhoods wn ON n.neighborhoodid = wn.neighborhoodid ");
            idQueryStringBuilder.append("WHERE wn.workerid = :workerId ");
        }

        // Add ordering to the first query
        idQueryStringBuilder.append("ORDER BY n.neighborhoodid");

        Query idQuery = em.createNativeQuery(idQueryStringBuilder.toString());
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        if (workerId != null) {
            idQuery.setParameter("workerId", workerId);
        }

        // If List<Long> is used it returns this error "Parameter value element [-1] did not match expected type [java.lang.Long (n/a)]"
        // There is an ID = -1 in the result set, the banned users
        List<?> result = idQuery.getResultList();
        List<Long> neighborhoodIds = result.stream()
                .map(id -> ((Number) id).longValue())
                .collect(Collectors.toList());
        if (neighborhoodIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Build the second query to fetch neighborhoods using the fetched IDs and order the results
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
    public int countNeighborhoods(Long workerId) {
        LOGGER.debug("Counting Neighborhoods with Worker Id {}", workerId);

        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("SELECT COUNT(DISTINCT n.neighborhoodid) FROM neighborhoods n ");

        if (workerId != null) {
            queryStringBuilder.append("JOIN workers_neighborhoods wn ON n.neighborhoodid = wn.neighborhoodid ");
            queryStringBuilder.append("WHERE wn.workerid = :workerId ");
        }

        Query nativeQuery = em.createNativeQuery(queryStringBuilder.toString());

        if (workerId != null) {
            nativeQuery.setParameter("workerId", workerId);
        }

        return Integer.parseInt((nativeQuery.getSingleResult()).toString());
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
