package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.models.Entities.Resource;
import ar.edu.itba.paw.models.Entities.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Repository
public class AvailabilityDaoImpl implements AvailabilityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // -------------------------------------------- AVAILABILITY INSERT ------------------------------------------------

    @Override
    public Availability createAvailability(long amenityId, long shiftId) {
        LOGGER.debug("Inserting Availability");
        Availability availability = new Availability.Builder()
                .amenity(em.find(Amenity.class, amenityId))
                .shift(em.find(Shift.class, shiftId))
                .build();
        em.persist(availability);
        return availability;
    }

    // -------------------------------------------- AVAILABILITY SELECT ------------------------------------------------

    @Override
    public Optional<Availability> findAvailability(long availabilityId) {
        LOGGER.debug("Selecting Availability with id {}", availabilityId);
        return Optional.ofNullable(em.find(Availability.class, availabilityId));
    }

    @Override
    public List<Availability> getAvailability(long id) {
        LOGGER.debug("Selecting Availability for Amenity {}", id);
        TypedQuery<Availability> query = em.createQuery("SELECT a FROM Availability a WHERE a.amenityAvailabilityId = :amenityAvailabilityId", Availability.class);
        query.setParameter("amenityAvailabilityId", id);
        return query.getResultList();
    }

    @Override
    public OptionalLong findAvailabilityId(long amenityId, long shiftId) {
        LOGGER.debug("Selecting Availability with amenityId {} and shiftId {}", amenityId, shiftId);
        TypedQuery<Long> query = em.createQuery("SELECT a.amenityAvailabilityId FROM Availability a WHERE a.shift.shiftId = :shiftId AND a.amenity.amenityId = :amenityId", Long.class);
        query.setParameter("shiftId", shiftId);
        query.setParameter("amenityId", amenityId);
        List<Long> resultList = query.getResultList();
        return resultList.isEmpty() ? OptionalLong.empty() : OptionalLong.of(resultList.get(0));
    }

    // -------------------------------------------- AVAILABILITY DELETE ------------------------------------------------

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