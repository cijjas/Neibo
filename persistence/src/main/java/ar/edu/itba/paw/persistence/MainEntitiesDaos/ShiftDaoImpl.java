package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.MainEntities.Day;
import ar.edu.itba.paw.models.MainEntities.Shift;
import ar.edu.itba.paw.models.MainEntities.Time;
import ar.edu.itba.paw.models.MainEntities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<Shift> findShiftById(long shiftId) {
        LOGGER.debug("Selecting Shift with shiftId {}", shiftId);
        return Optional.ofNullable(em.find(Shift.class, shiftId));
    }

    @Override
    public Optional<Shift> findShiftId(long startTime, long dayId) {
        LOGGER.debug("Selecting Shift with startTime {} and dayId {}", startTime, dayId);
        String jpql = "SELECT s FROM Shift s " +
                "JOIN s.day d " +
                "WHERE s.startTime.timeId = :startTime AND d.dayId = :dayId";
        TypedQuery<Shift> query = em.createQuery(jpql, Shift.class)
                .setParameter("startTime", startTime)
                .setParameter("dayId", dayId);
        List<Shift> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Shift> getShifts(long amenityId, long dayId, Date date) {
        List<Object[]> results = em.createNativeQuery(
                        "SELECT s.shiftid, s.dayid, s.starttime, " +
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
                                "WHERE amenityid = :amenityId AND dayid = :dayId"
                )
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

    @Override
    public List<Shift> getAmenityShifts(long amenityId) {
        LOGGER.debug("Selecting Weekly Available Shifts for Amenity {}", amenityId);

        String hql = "SELECT s FROM Shift s " +
                "JOIN s.amenities a " +
                "WHERE a.amenityId = :amenityId";

        TypedQuery<Shift> query = em.createQuery(hql, Shift.class);
        query.setParameter("amenityId", amenityId);

        return query.getResultList();
    }
}