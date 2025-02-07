package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class AmenityDaoImpl implements AmenityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- AMENITIES INSERT ---------------------------------------------------

    @Override
    public Amenity createAmenity(long neighborhoodId, String description, String name) {
        LOGGER.debug("Inserting Amenity with Neighborhood Id {}", neighborhoodId);

        Amenity amenity = new Amenity.Builder()
                .name(name)
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .description(description)
                .build();
        em.persist(amenity);
        return amenity;
    }

    // ---------------------------------------------- AMENITIES SELECT ---------------------------------------------------

    @Override
    public Optional<Amenity> findAmenity(long neighborhoodId, long amenityId) {
        LOGGER.debug("Selecting Amenity with Neighborhood Id {} and Amenity Id {}", neighborhoodId, amenityId);

        TypedQuery<Amenity> query = em.createQuery(
                "SELECT a FROM Amenity a WHERE a.amenityId = :amenityId AND a.neighborhood.neighborhoodId = :neighborhoodId",
                Amenity.class
        );

        query.setParameter("amenityId", amenityId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Amenity> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Amenity> getAmenities(long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Amenity with Neighborhood Id {}", neighborhoodId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Amenity> idRoot = idQuery.from(Amenity.class);
        idQuery.select(idRoot.get("amenityId"));
        Join<Amenity, Neighborhood> neighborhoodJoin = idRoot.join("neighborhood");
        idQuery.where(cb.equal(neighborhoodJoin.get("neighborhoodId"), neighborhoodId));
        idQuery.orderBy(cb.desc(idRoot.get("amenityId")));
        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);
        List<Long> amenityIds = idTypedQuery.getResultList();
        if (amenityIds.isEmpty())
            return Collections.emptyList();
        CriteriaQuery<Amenity> dataQuery = cb.createQuery(Amenity.class);
        Root<Amenity> dataRoot = dataQuery.from(Amenity.class);
        dataQuery.where(dataRoot.get("amenityId").in(amenityIds));
        dataQuery.orderBy(cb.desc(dataRoot.get("amenityId")));
        TypedQuery<Amenity> dataTypedQuery = em.createQuery(dataQuery);
        return dataTypedQuery.getResultList();
    }

    @Override
    public int countAmenities(long neighborhoodId) {
        LOGGER.debug("Counting Amenity with Neighborhood Id {}", neighborhoodId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Amenity> root = criteriaQuery.from(Amenity.class);
        Join<Amenity, Neighborhood> neighborhoodJoin = root.join("neighborhood");
        criteriaQuery.select(cb.count(root));
        criteriaQuery.where(cb.equal(neighborhoodJoin.get("neighborhoodId"), neighborhoodId));
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult().intValue();
    }

    // ---------------------------------------------- AMENITIES DELETE ---------------------------------------------------

    @Override
    public boolean deleteAmenity(long neighborhoodId, long amenityId) {
        LOGGER.debug("Deleting Amenity with Neighborhood Id {} and Amenity Id {}", neighborhoodId, amenityId);

        int deletedCount = em.createQuery("DELETE FROM Amenity WHERE amenityId = :amenityId AND neighborhood.id = :neighborhoodId")
                .setParameter("amenityId", amenityId)
                .setParameter("neighborhoodId", neighborhoodId)
                .executeUpdate();
        return deletedCount > 0;
    }
}
