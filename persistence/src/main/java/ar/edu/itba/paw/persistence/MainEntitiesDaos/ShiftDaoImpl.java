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
import java.util.ArrayList;
import java.util.Date;
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
        LOGGER.debug("Inserting Shift with Day Id {} and Start Time Id {}", dayId, startTimeId);

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
        LOGGER.debug("Selecting Shift with Shift Id {}", shiftId);

        return Optional.ofNullable(em.find(Shift.class, shiftId));
    }

    @Override
    public List<Shift> getShifts(Long amenityId, Date date, Integer dayId) {
        LOGGER.debug("Selecting Shifts with Amenity Id {} on Date {}", amenityId, date);

        if (amenityId == null && date == null) {
            return em.createQuery("SELECT s FROM Shift s ORDER BY s.day DESC, s.startTime ASC", Shift.class)
                    .getResultList();
        }

        if (date == null) {
            String hql = "SELECT s FROM Shift s JOIN s.amenities a WHERE a.amenityId = :amenityId ORDER BY s.day DESC, s.startTime ASC";
            TypedQuery<Shift> query = em.createQuery(hql, Shift.class);
            query.setParameter("amenityId", amenityId);
            return query.getResultList();
        }

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
                "WHERE amenityid = :amenityId AND dayid = :dayId ORDER BY s.dayid DESC, s.starttime ASC";

        List<Object[]> results = em.createNativeQuery(nativeQuery)
                .setParameter("date", date)
                .setParameter("amenityId", amenityId)
                .setParameter("dayId", dayId)
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