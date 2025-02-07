package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingDaoImpl implements BookingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingDaoImpl.class);
    private final String BOOKINGS_JOIN_AVAILABILITY =
            "SELECT uav.*, bookingid, date, a.amenityid, s.shiftid, userid, a.name, d.dayid, t.timeid, timeinterval\n" +
                    "FROM users_availability uav\n" +
                    "INNER JOIN amenities_shifts_availability asa ON uav.amenityavailabilityid = asa.amenityavailabilityid\n" +
                    "INNER JOIN amenities a ON asa.amenityid = a.amenityid\n" +
                    "INNER JOIN shifts s ON s.shiftid = asa.shiftid\n" +
                    "INNER JOIN days d ON s.dayid = d.dayid\n" +
                    "INNER JOIN times t ON s.starttime = t.timeid";
    @PersistenceContext
    private EntityManager em;

    // --------------------------------------------- BOOKINGS INSERT ---------------------------------------------------

    @Override
    public Booking createBooking(long userId, long amenityAvailabilityId, Date reservationDate) {
        LOGGER.debug("Inserting Booking with User Id {} and Availability Id {}", userId, amenityAvailabilityId);

        Booking booking = new Booking.Builder()
                .user(em.find(User.class, userId))
                .bookingDate(reservationDate)
                .amenityAvailability(em.find(Availability.class, amenityAvailabilityId))
                .build();
        em.persist(booking);
        return booking;
    }

    // --------------------------------------------- BOOKINGS SELECT ---------------------------------------------------

    @Override
    public Optional<Booking> findBooking(long neighborhoodId, long bookingId) {
        LOGGER.debug("Selecting Booking with Neighborhood Id {} and Booking Id {}", neighborhoodId, bookingId);

        TypedQuery<Booking> query = em.createQuery(
                "SELECT b FROM Booking b WHERE b.bookingId = :bookingId AND b.user.neighborhood.neighborhoodId = :neighborhoodId",
                Booking.class
        );

        query.setParameter("bookingId", bookingId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Booking> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Booking> getBookings(long neighborhoodId, Long userId, Long amenityId, int page, int size) {
        LOGGER.debug("Selecting Bookings with Neighborhood Id {}, User Id {}, and Amenity Id {}", neighborhoodId, userId, amenityId);

        StringBuilder query = new StringBuilder(BOOKINGS_JOIN_AVAILABILITY);
        query.append(" WHERE 1 = 1");

        if (userId != null) {
            query.append(" AND userid = :userId");
        }

        if (amenityId != null) {
            query.append(" AND asa.amenityid = :amenityId");
        }

        query.append(" AND a.neighborhoodid = :neighborhoodId");

        query.append(" ORDER BY uav.date ASC, asa.amenityid, timeinterval ASC");
        query.append(" LIMIT :limit OFFSET :offset");

        String sql = query.toString();

        Query sqlQuery = em.createNativeQuery(sql, Booking.class);

        if (userId != null) {
            sqlQuery.setParameter("userId", userId);
        }

        if (amenityId != null) {
            sqlQuery.setParameter("amenityId", amenityId);
        }

        sqlQuery.setParameter("neighborhoodId", neighborhoodId);

        int offset = (page - 1) * size;
        sqlQuery.setParameter("limit", size);
        sqlQuery.setParameter("offset", offset);

        return sqlQuery.getResultList();
    }

    @Override
    public int countBookings(long neighborhoodId, Long userId, Long amenityId) {
        LOGGER.debug("Counting Bookings with Neighborhood Id {}, User Id {}, and Amenity Id {}", neighborhoodId, userId, amenityId);

        StringBuilder countQuery = new StringBuilder("WITH CountCTE AS (SELECT COUNT(*) FROM ");
        countQuery.append(" users_availability uav ");
        countQuery.append(" INNER JOIN amenities_shifts_availability asa ON uav.amenityavailabilityid = asa.amenityavailabilityid ");
        countQuery.append(" INNER JOIN amenities a ON asa.amenityid = a.amenityid ");
        countQuery.append(" INNER JOIN shifts s ON s.shiftid = asa.shiftid ");
        countQuery.append(" INNER JOIN days d ON s.dayid = d.dayid ");
        countQuery.append(" INNER JOIN times t ON s.starttime = t.timeid ");
        countQuery.append(" WHERE 1 = 1");

        if (userId != null) {
            countQuery.append(" AND uav.userid = :userId");
        }

        if (amenityId != null) {
            countQuery.append(" AND asa.amenityid = :amenityId");
        }

        countQuery.append(" AND a.neighborhoodid = :neighborhoodId");

        countQuery.append(") SELECT * FROM CountCTE");

        String sql = countQuery.toString();

        Query sqlQuery = em.createNativeQuery(sql);

        if (userId != null) {
            sqlQuery.setParameter("userId", userId);
        }

        if (amenityId != null) {
            sqlQuery.setParameter("amenityId", amenityId);
        }

        sqlQuery.setParameter("neighborhoodId", neighborhoodId);

        return ((Number) sqlQuery.getSingleResult()).intValue();
    }

    // ---------------------------------------- USERS_AVAILABILITY DELETE ----------------------------------------------

    @Override
    public boolean deleteBooking(long neighborhoodId, long bookingId) {
        LOGGER.debug("Deleting Booking with Neighborhood Id {} and Booking Id {}", neighborhoodId, bookingId);

        String nativeSql = "DELETE FROM users_availability b " +
                "WHERE b.bookingid = :bookingId " +
                "AND b.amenityavailabilityid IN ( " +
                "    SELECT aa.amenityavailabilityid " +
                "    FROM amenities_shifts_availability aa " +
                "    JOIN amenities a ON aa.amenityid = a.amenityid " +
                "    WHERE a.neighborhoodid = :neighborhoodId" +
                ")";

        int deletedCount = em.createNativeQuery(nativeSql)
                .setParameter("bookingId", bookingId)
                .setParameter("neighborhoodId", neighborhoodId)
                .executeUpdate();

        return deletedCount > 0;
    }
}
