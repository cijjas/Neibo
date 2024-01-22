package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.enums.DayOfTheWeek;
import ar.edu.itba.paw.enums.ShiftStatus;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

        TypedQuery<Availability> query = em.createQuery("SELECT a FROM Availability a WHERE a.amenity.id = :amenityId", Availability.class);
        query.setParameter("amenityId", id);
        return query.getResultList();
    }

    @Override
    public List<Availability> getAvailability(long amenityId, String status, String date) {
        LOGGER.debug("Selecting Availability with status {} on date {} Amenity {}", status, date, amenityId);

        StringBuilder nativeQuery = new StringBuilder("SELECT a.* FROM amenities_shifts_availability a");
        nativeQuery.append(" JOIN amenities_shifts_availability asa ON a.amenityAvailabilityId = asa.amenityAvailabilityId");
        nativeQuery.append(" JOIN shifts s ON asa.shiftId = s.shiftId");
        nativeQuery.append(" WHERE a.amenityId = :amenityId");

        long dayOfWeek = -1;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                calendar.setTime(dateFormat.parse(date));
            } catch (ParseException e) {
                // this should never activate due to service validation
                throw new IllegalArgumentException("Invalid value (" + date + ") for the 'date' parameter. Please use a date in YYYY-(M)M-(D)D format.");
            }
            dayOfWeek = DayOfTheWeek.convertToCustomDayId(calendar.get(Calendar.DAY_OF_WEEK));
            System.out.println(dayOfWeek);
            nativeQuery.append(" AND s.dayId = :dayOfWeek");
        }

        if (status != null) {
            ShiftStatus shiftStatus = ShiftStatus.valueOf(status.toUpperCase());
            switch (shiftStatus) {
                case FREE:
                    nativeQuery.append(" AND NOT EXISTS (SELECT ua.amenityAvailabilityId FROM users_availability ua WHERE ua.amenityAvailabilityId = a.amenityAvailabilityId");
                    if (date != null) {
                        nativeQuery.append(" AND ua.date = to_date(:date, 'YYYY-MM-DD')");
                    }
                    nativeQuery.append(")");
                    break;
                case TAKEN:
                    nativeQuery.append(" AND EXISTS (SELECT ua.amenityAvailabilityId FROM users_availability ua WHERE ua.amenityAvailabilityId = a.amenityAvailabilityId");
                    if (date != null) {
                        nativeQuery.append(" AND ua.date = to_date(:date, 'YYYY-MM-DD')");
                    }
                    nativeQuery.append(")");
                    break;
            }
        }

        System.out.println(nativeQuery);
        Query query = em.createNativeQuery(nativeQuery.toString(), Availability.class);
        query.setParameter("amenityId", amenityId);

        // Set parameters if present
        if (date != null && dayOfWeek != -1) {
            query.setParameter("dayOfWeek", dayOfWeek);
        }
        if (date != null && status != null) {
            query.setParameter("date", date);
        }

        // Execute the query and return the result
        return query.getResultList();
    }



    @Override
    public OptionalLong findId(long amenityId, long shiftId) {
        LOGGER.debug("Selecting Availability with amenityId {} and shiftId {}", amenityId, shiftId);

        TypedQuery<Long> query = em.createQuery("SELECT a.amenityAvailabilityId FROM Availability a WHERE a.shift.shiftId = :shiftId AND a.amenity.amenityId = :amenityId", Long.class);
        query.setParameter("shiftId", shiftId);
        query.setParameter("amenityId", amenityId);
        List<Long> resultList = query.getResultList();
        return resultList.isEmpty() ? OptionalLong.empty() : OptionalLong.of(resultList.get(0));
    }

    @Override
    public Optional<Availability> findAvailability(long amenityId, long availabilityId, long neighborhoodId) {
        LOGGER.debug("Selecting Availability with amenity Id {} and availability Id {}", amenityId, availabilityId);

        TypedQuery<Availability> query = em.createQuery("SELECT a FROM Availability a WHERE a.amenityAvailabilityId = :availabilityId AND a.amenity.amenityId = :amenityId AND a.amenity.neighborhood.id = :neighborhoodId", Availability.class);
        query.setParameter("availabilityId", availabilityId);
        query.setParameter("amenityId", amenityId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Availability> resultList = query.getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
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