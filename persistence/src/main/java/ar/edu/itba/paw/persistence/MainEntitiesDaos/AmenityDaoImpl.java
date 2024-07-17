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
    public Amenity createAmenity(String name, String description, long neighborhoodId) {
        LOGGER.debug("Inserting Amenity");

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
    public Optional<Amenity> findAmenity(long amenityId) {
        LOGGER.debug("Selecting Amenity with id {}", amenityId);

        return Optional.ofNullable(em.find(Amenity.class, amenityId));
    }

    @Override
    public Optional<Amenity> findAmenity(long amenityId, long neighborhoodId) {
        LOGGER.debug("Selecting Amenity with amenityId {} and neighborhoodId {}", amenityId, neighborhoodId);

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
        LOGGER.debug("Selecting Amenity with neighborhoodId {}", neighborhoodId);

        // Initialize Query Builder
        CriteriaBuilder cb = em.getCriteriaBuilder();
        // We retrieve a list of amenity ids which are Longs, from the Amenity Entity
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Amenity> idRoot = idQuery.from(Amenity.class);
        idQuery.select(idRoot.get("amenityId"));
        // We join through the neighborhoodId
        Join<Amenity, Neighborhood> neighborhoodJoin = idRoot.join("neighborhood");
        idQuery.where(cb.equal(neighborhoodJoin.get("neighborhoodId"), neighborhoodId));
        idQuery.orderBy(cb.asc(idRoot.get("amenityId")));
        // Create the query
        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        // We implement pagination in the query
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);
        // Results
        List<Long> amenityIds = idTypedQuery.getResultList();
        // Check if amenityIds is empty, better performance
        if (amenityIds.isEmpty())
            return Collections.emptyList();
        // Second Query is also focused on Amenities
        CriteriaQuery<Amenity> dataQuery = cb.createQuery(Amenity.class);
        Root<Amenity> dataRoot = dataQuery.from(Amenity.class);
        // Add predicate that enforces existence within the IDs recovered in the first query
        dataQuery.where(dataRoot.get("amenityId").in(amenityIds));
        // Order by amenityId in the final query as well
        dataQuery.orderBy(cb.asc(dataRoot.get("amenityId")));
        // Create!
        TypedQuery<Amenity> dataTypedQuery = em.createQuery(dataQuery);
        // Return Results
        return dataTypedQuery.getResultList();
    }

    // ---------------------------------------------------

    @Override
    public int countAmenities(long neighborhoodId) {
        LOGGER.debug("Counting Amenity with neighborhoodId {}", neighborhoodId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Amenity> root = criteriaQuery.from(Amenity.class);
        Join<Amenity, Neighborhood> neighborhoodJoin = root.join("neighborhood"); // Use the association
        criteriaQuery.select(cb.count(root));
        criteriaQuery.where(cb.equal(neighborhoodJoin.get("neighborhoodId"), neighborhoodId)); // Filter by neighborhoodId
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult().intValue();
    }

    // ---------------------------------------------- AMENITIES DELETE ---------------------------------------------------

    @Override
    public boolean deleteAmenity(long amenityId) {
        LOGGER.debug("Deleting Amenity with id {}", amenityId);

        int deletedCount = em.createQuery("DELETE FROM Amenity WHERE amenityId = :amenityId ")
                .setParameter("amenityId", amenityId)
                .executeUpdate();
        return deletedCount > 0;
    }
}
