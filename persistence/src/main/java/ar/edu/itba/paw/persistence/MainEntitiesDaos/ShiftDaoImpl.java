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
import java.util.Date;
import java.util.ArrayList;
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
    public List<Shift> getShifts(Long amenityId, Date date) {
        LOGGER.debug("Selecting Weekly Available Shifts for Amenity {} on Date {}",
                amenityId != null ? amenityId : "ALL", date != null ? date : "ALL DATES");

        // If both amenityId and date are null, return all shifts ordered by day and start time.
        if (amenityId == null && date == null) {
            return em.createQuery("SELECT s FROM Shift s ORDER BY s.day DESC, s.startTime DESC", Shift.class)
                    .getResultList();
        }

        // If date is null, use HQL to fetch shifts for a specific amenity.
        if (date == null) {
            String hql = "SELECT s FROM Shift s JOIN s.amenities a WHERE a.amenityId = :amenityId ORDER BY s.day DESC, s.startTime DESC";
            TypedQuery<Shift> query = em.createQuery(hql, Shift.class);
            query.setParameter("amenityId", amenityId);
            return query.getResultList();
        }

        // If both amenityId and date are provided, use a native query to fetch shifts with availability status.
        String nativeQuery = "SELECT s.shiftid, s.dayid, s.starttime, " +
                "CASE " +
                "   WHEN (" +
                "       a.amenityavailabilityid IN " +
                "       (SELECT ua.amenityavailabilityid " +
                "        FROM users_availability ua " +
                "        WHERE date = :date)" +
                "   ) THEN true " +
                "   ELSE false " +
                "END AS taken " +
                "FROM shifts s " +
                "JOIN amenities_shifts_availability a ON s.shiftId = a.shiftId " +
                "INNER JOIN times t ON s.starttime = t.timeid " +
                "WHERE amenityid = :amenityId";

        List<Object[]> results = em.createNativeQuery(nativeQuery)
                .setParameter("date", date)
                .setParameter("amenityId", amenityId)
                .getResultList();

        List<Shift> shifts = new ArrayList<>();
        for (Object[] row : results) {
            Shift shift = new Shift.Builder()
                    .shiftId(((Number) row[0]).longValue())
                    .day(em.find(Day.class, ((Number) row[1]).longValue()))
                    .startTime(em.find(Time.class, ((Number) row[2]).longValue()))
                    .taken((Boolean) row[3])
                    .build();
            shifts.add(shift);
        }

        return shifts;
    }
}