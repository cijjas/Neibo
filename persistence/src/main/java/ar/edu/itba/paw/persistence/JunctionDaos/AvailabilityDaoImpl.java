package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.MainEntities.Amenity;
import ar.edu.itba.paw.models.JunctionEntities.Availability;
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
import javax.sql.DataSource;
import java.util.List;
import java.util.OptionalLong;

@Repository
public class AvailabilityDaoImpl implements AvailabilityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private static final RowMapper<Long> ROW_MAPPER = (rs, rowNum) -> {
        return rs.getLong("amenityavailabilityid");
    };
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private AmenityDao amenityDao;
    private ShiftDao shiftDao;

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY INSERT -----------------------------------------

    @Autowired
    public AvailabilityDaoImpl(final DataSource ds, final AmenityDao amenityDao, final ShiftDao shiftDao) {
        this.amenityDao = amenityDao;
        this.shiftDao = shiftDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(Table.amenities_shifts_availability.name())
                .usingGeneratedKeyColumns("amenityavailabilityid");
    }

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY SELECT -----------------------------------------

    @Override
    public Availability createAvailability(long amenityId, long shiftId) {
        LOGGER.debug("Inserting Availability");

        // Fetch the Amenity and Shift entities using the EntityManager
        Amenity amenity = em.find(Amenity.class, amenityId);
        Shift shift = em.find(Shift.class, shiftId);

        // Check if the Amenity and Shift entities exist
        if (amenity == null) {
            throw new NotFoundException("Amenity Not Found");
        }
        if (shift == null) {
            throw new NotFoundException("Shift Not Found");
        }

        // Create and persist the Availability
        Availability availability = new Availability.Builder()
                .amenity(amenity)
                .shift(shift)
                .build();
        em.persist(availability);

        return availability;
    }


    public OptionalLong findAvailabilityId(long amenityId, long shiftId) {
        LOGGER.debug("Selecting Availability with amenityId {} and shiftId {}", amenityId, shiftId);
        TypedQuery<Long> query = em.createQuery("SELECT a.availabilityId FROM Availability a WHERE a.shift.shiftId = :shiftId AND a.amenity.amenityId = :amenityId", Long.class);
        query.setParameter("shiftId", shiftId);
        query.setParameter("amenityId", amenityId);
        List<Long> resultList = query.getResultList();
        return resultList.isEmpty() ? OptionalLong.empty() : OptionalLong.of(resultList.get(0));
    }



    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY DELETE -----------------------------------------

    @Override
    public boolean deleteAvailability(long amenityId, long shiftId) {
        LOGGER.debug("Deleting Availability with amenityId {} and shiftId {}", amenityId, shiftId);
        int deletedCount = em.createQuery("DELETE FROM Availability a WHERE a.amenity.amenityId = :amenityId AND a.shift.shiftId = :shiftId")
                .setParameter("amenityId", amenityId)
                .setParameter("shiftId", shiftId)
                .executeUpdate();
        return deletedCount > 0;
    }


}