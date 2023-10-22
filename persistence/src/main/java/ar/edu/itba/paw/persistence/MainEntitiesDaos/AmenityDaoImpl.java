package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.MainEntities.Amenity;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;
import ar.edu.itba.paw.models.MainEntities.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class AmenityDaoImpl implements AmenityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private ShiftDao shiftDao;
    private NeighborhoodDao neighborhoodDao;

    private final RowMapper<Amenity> ROW_MAPPER = (rs, rowNum) -> {
        List<Shift> availableShifts = shiftDao.getAmenityShifts(rs.getLong("amenityid"));
        Neighborhood neighborhood = neighborhoodDao.findNeighborhoodById(rs.getLong("neighborhoodid")).orElseThrow(()->new NotFoundException("Neighborhood Not Found"));
        return new Amenity.Builder()
                .amenityId(rs.getLong("amenityid"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .neighborhood(neighborhood)
                .availableShifts(availableShifts)
                .build();
    };
    private String AMENITIES = "SELECT * FROM amenities";
    private String COUNT_AMENITIES = "SELECT COUNT(*) FROM amenities";

    // ---------------------------------------------- AMENITY INSERT ---------------------------------------------------

    @Autowired
    public AmenityDaoImpl(final DataSource ds, final ShiftDao shiftDao, final NeighborhoodDao neighborhoodDao) {
        this.neighborhoodDao = neighborhoodDao;
        this.shiftDao = shiftDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("amenityid")
                .withTableName(Table.amenities.name());
    }

    // ---------------------------------------------- AMENITY SELECT ---------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, long neighborhoodId) {
        LOGGER.debug("Inserting Amenity {}", name);
        Amenity amenity = new Amenity.Builder()
                .name(name)
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .description(description)
                .build();
        em.persist(amenity);
        return amenity;
    }

    @Override
    public Optional<Amenity> findAmenityById(long amenityId) {
        LOGGER.debug("Selecting Amenity with id {}", amenityId);
        return Optional.ofNullable(em.find(Amenity.class, amenityId));
    }

    @Override
    public List<Amenity> getAmenities(long neighborhoodId, int page, int size) {
        // Initialize Query Builder
        CriteriaBuilder cb = em.getCriteriaBuilder();
        // We retrieve a list of amenity ids which are Longs, from the Amenity Entity
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Amenity> idRoot = idQuery.from(Amenity.class);
        idQuery.select(idRoot.get("amenityId"));
        // We join through the neighborhoodId
        Join<Amenity, Neighborhood> neighborhoodJoin = idRoot.join("neighborhood");
        idQuery.where(cb.equal(neighborhoodJoin.get("neighborhoodId"), neighborhoodId));
        // Create the query
        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        // We implement pagination in the query
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);
        // Results
        List<Long> amenityIds = idTypedQuery.getResultList();
        // Second Query is also focused on Amenities
        CriteriaQuery<Amenity> dataQuery = cb.createQuery(Amenity.class);
        Root<Amenity> dataRoot = dataQuery.from(Amenity.class);
        // Add predicate that enforces existence within the IDs recovered in the first query
        dataQuery.where(dataRoot.get("amenityId").in(amenityIds));
        TypedQuery<Amenity> dataTypedQuery = em.createQuery(dataQuery);
        // Return Results
        return dataTypedQuery.getResultList();
    }


    @Override
    public int getAmenitiesCount(long neighborhoodId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Amenity> root = criteriaQuery.from(Amenity.class);
        Join<Amenity, Neighborhood> neighborhoodJoin = root.join("neighborhood"); // Use the association
        criteriaQuery.select(cb.count(root));
        criteriaQuery.where(cb.equal(neighborhoodJoin.get("neighborhoodId"), neighborhoodId)); // Filter by neighborhoodId
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult().intValue();
    }


    // ---------------------------------------------- AMENITY DELETE ---------------------------------------------------

    @Override
    public boolean deleteAmenity(long amenityId) {
        LOGGER.debug("Deleting Amenity with id {}", amenityId);
        int deletedCount = em.createQuery("DELETE FROM Amenity WHERE amenityId = :amenityId ")
                .setParameter("amenityId", amenityId)
                .executeUpdate();
        return deletedCount > 0;
    }
}
