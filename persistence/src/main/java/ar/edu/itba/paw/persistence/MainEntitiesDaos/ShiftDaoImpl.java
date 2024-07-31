package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.Entities.Day;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.models.Entities.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class ShiftDaoImpl implements ShiftDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------------- SHIFTS INSERT ---------------------------------------------------

    @Override
    public Shift createShift(long dayId, long startTimeId) {
        LOGGER.debug("Inserting Shift");

        Shift shift = new Shift.Builder()
                .day(em.find(Day.class, dayId))
                .startTime(em.find(Time.class, startTimeId))
                .build();
        em.persist(shift);
        return shift;
    }

    // ----------------------------------------------- SHIFTS SELECT ---------------------------------------------------

    @Override
    public Optional<Shift> findShift(long shiftId) {
        LOGGER.debug("Selecting Shift with shiftId {}", shiftId);

        return Optional.ofNullable(em.find(Shift.class, shiftId));
    }

    @Override
    public List<Shift> getShifts(Long amenityId) {
        LOGGER.debug("Selecting Weekly Available Shifts for Amenity {}", amenityId != null ? amenityId : "ALL");

        StringBuilder hql = new StringBuilder("SELECT s FROM Shift s JOIN s.amenities a");
        if (amenityId != null) {
            hql.append(" WHERE a.amenityId = :amenityId");
        }

        hql.append(" ORDER BY s.day DESC, s.startTime DESC");

        TypedQuery<Shift> query = em.createQuery(hql.toString(), Shift.class);
        if (amenityId != null) {
            query.setParameter("amenityId", amenityId);
        }

        return query.getResultList();
    }
}