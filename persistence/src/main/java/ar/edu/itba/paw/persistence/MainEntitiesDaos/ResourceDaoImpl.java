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
    public Optional<Resource> findResource(final long resourceId) {
        LOGGER.debug("Selecting Resource with resourceId {}", resourceId);

        return Optional.ofNullable(em.find(Resource.class, resourceId));
    }

    public List<Resource> getResources(long neighborhoodId) {
        LOGGER.debug("Selecting Resources from Neighborhood {}", neighborhoodId);

        TypedQuery<Resource> query = em.createQuery("SELECT r FROM Resource r WHERE r.neighborhood.neighborhoodId = :neighborhoodId", Resource.class);
        query.setParameter("neighborhoodId", neighborhoodId);
        return query.getResultList();
    }

    // --------------------------------------------- RESOURCE DELETE ----------------------------------------------------

    public boolean deleteResource(long resourceId) {
        LOGGER.debug("Deleting Resource with resourceId {}", resourceId);

        Resource resource = em.find(Resource.class, resourceId);
        if (resource != null) {
            em.remove(resource);
            return true;
        }
        return false;
    }
}
