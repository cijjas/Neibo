package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.models.Entities.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class AvailabilityDaoImpl implements AvailabilityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // -------------------------------------------- AVAILABILITY INSERT ------------------------------------------------

    @Override
    public Availability createAvailability(long shiftId, long amenityId) {
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
    public Optional<Availability> findAvailability(long shiftId, long amenityId) {
        LOGGER.debug("Selecting Availability with amenityId {} and shiftId {}", amenityId, shiftId);

        TypedQuery<Availability> query = em.createQuery("SELECT a FROM Availability a WHERE a.shift.shiftId = :shiftId AND a.amenity.amenityId = :amenityId", Availability.class);
        query.setParameter("shiftId", shiftId);
        query.setParameter("amenityId", amenityId);
        List<Availability> resultList = query.getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    // -------------------------------------------- AVAILABILITY DELETE ------------------------------------------------

    @Override
    public boolean deleteAvailability(long shiftId, long amenityId) {
        LOGGER.debug("Deleting Availability with amenityId {} and shiftId {}", amenityId, shiftId);

        int deletedCount = em.createQuery("DELETE FROM Availability a WHERE a.amenity.amenityId = :amenityId AND a.shift.shiftId = :shiftId")
                .setParameter("amenityId", amenityId)
                .setParameter("shiftId", shiftId)
                .executeUpdate();
        return deletedCount > 0;
    }
}