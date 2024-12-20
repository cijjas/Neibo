package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class ResourceDaoImpl implements ResourceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // --------------------------------------------- RESOURCES INSERT --------------------------------------------------

    @Override
    public Resource createResource(long neighborhoodId, String title, String description, long imageId) {
        LOGGER.debug("Inserting Resource {}", title);

        Resource resource = new Resource.Builder()
                .title(title)
                .description(description)
                .image(em.find(Image.class, imageId))
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .build();
        em.persist(resource);
        return resource;
    }

    // --------------------------------------------- RESOURCES SELECT --------------------------------------------------

    @Override
    public Optional<Resource> findResource(long neighborhoodId, long resourceId) {
        LOGGER.debug("Selecting Resource with resourceId {} and neighborhoodId {}", resourceId, neighborhoodId);

        TypedQuery<Resource> query = em.createQuery(
                "SELECT r FROM Resource r WHERE r.resourceId = :resourceId AND r.neighborhood.neighborhoodId = :neighborhoodId",
                Resource.class
        );
        query.setParameter("resourceId", resourceId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Resource> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Resource> getResources(long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting paginated Resources from Neighborhood {}", neighborhoodId);

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Resource> idRoot = idQuery.from(Resource.class);
        idQuery.select(idRoot.get("resourceId"));
        idQuery.where(cb.equal(idRoot.get("neighborhood").get("neighborhoodId"), neighborhoodId));
        idQuery.orderBy(cb.asc(idRoot.get("resourceId")));
        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);
        List<Long> resourceIds = idTypedQuery.getResultList();

        if (resourceIds.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaQuery<Resource> dataQuery = cb.createQuery(Resource.class);
        Root<Resource> dataRoot = dataQuery.from(Resource.class);
        dataQuery.where(dataRoot.get("resourceId").in(resourceIds));
        dataQuery.orderBy(cb.asc(dataRoot.get("resourceId")));
        TypedQuery<Resource> dataTypedQuery = em.createQuery(dataQuery);

        return dataTypedQuery.getResultList();
    }

    @Override
    public int countResources(long neighborhoodId) {
        LOGGER.debug("Counting Resources from Neighborhood {}", neighborhoodId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Resource> root = criteriaQuery.from(Resource.class);
        criteriaQuery.select(cb.count(root));
        criteriaQuery.where(cb.equal(root.get("neighborhood").get("neighborhoodId"), neighborhoodId));
        TypedQuery<Long> query = em.createQuery(criteriaQuery);

        return query.getSingleResult().intValue();
    }

    // --------------------------------------------- RESOURCE DELETE ----------------------------------------------------

    @Override
    public boolean deleteResource(long neighborhoodId, long resourceId) {
        LOGGER.debug("Deleting Resource with resourceId {} and neighborhoodId {}", resourceId, neighborhoodId);

        String hql = "DELETE FROM Resource r WHERE r.resourceId = :resourceId " +
                "AND r.neighborhood.id = :neighborhoodId";

        int deletedCount = em.createQuery(hql)
                .setParameter("resourceId", resourceId)
                .setParameter("neighborhoodId", neighborhoodId)
                .executeUpdate();

        return deletedCount > 0;
    }
}
