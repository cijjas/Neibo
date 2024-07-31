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
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingDaoImpl implements BookingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    private final String BOOKINGS_JOIN_AVAILABILITY =
            "SELECT uav.*, bookingid, date, a.amenityid, s.shiftid, userid, a.name, d.dayid, t.timeid, timeinterval\n" +
                    "FROM users_availability uav\n" +
                    "INNER JOIN amenities_shifts_availability asa ON uav.amenityavailabilityid = asa.amenityavailabilityid\n" +
                    "INNER JOIN amenities a ON asa.amenityid = a.amenityid\n" +
                    "INNER JOIN shifts s ON s.shiftid = asa.shiftid\n" +
                    "INNER JOIN days d ON s.dayid = d.dayid\n" +
                    "INNER JOIN times t ON s.starttime = t.timeid";
    // --------------------------------------------- BOOKINGS INSERT ---------------------------------------------------

    @Override
    public Booking createBooking(long userId, long amenityAvailabilityId, Date reservationDate) {
        LOGGER.debug("Inserting Booking");

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
    public Optional<Booking> findBooking(long bookingId) {
        LOGGER.debug("Selecting Booking with id {}", bookingId);

        return Optional.ofNullable(em.find(Booking.class, bookingId));
    }


    @Override
    public List<Booking> getBookings(Long userId, Long amenityId, int page, int size) {
        LOGGER.debug("Selecting Bookings from userId {} and amenityId {}", userId, amenityId);

        StringBuilder query = new StringBuilder(BOOKINGS_JOIN_AVAILABILITY);
        query.append(" WHERE 1 = 1");

        if (userId != null) {
            query.append(" AND userid = :userId");
        }

        if (amenityId != null) {
            query.append(" AND asa.amenityid = :amenityId");
        }

        query.append(" ORDER BY uav.date, asa.amenityid, timeinterval asc");
        query.append(" LIMIT :limit OFFSET :offset");

        String sql = query.toString();

        // Specify the result class (Booking.class) in createNativeQuery
        Query sqlQuery = em.createNativeQuery(sql, Booking.class);

        if (userId != null) {
            sqlQuery.setParameter("userId", userId);
        }

        if (amenityId != null) {
            sqlQuery.setParameter("amenityId", amenityId);
        }

        int offset = (page - 1) * size;
        sqlQuery.setParameter("limit", size);
        sqlQuery.setParameter("offset", offset);

        return sqlQuery.getResultList();
    }

    @Override
    public int countBookings(Long userId, Long amenityId) {
        LOGGER.debug("Counting Bookings for userId {} and amenityId {}", userId, amenityId);

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

        countQuery.append(") SELECT * FROM CountCTE");

        String sql = countQuery.toString();

        Query sqlQuery = em.createNativeQuery(sql);

        if (userId != null) {
            sqlQuery.setParameter("userId", userId);
        }

        if (amenityId != null) {
            sqlQuery.setParameter("amenityId", amenityId);
        }

        return ((Number) sqlQuery.getSingleResult()).intValue();
    }

    // ---------------------------------------- USERS_AVAILABILITY DELETE ----------------------------------------------

    @Override
    public boolean deleteBooking(long bookingId) {
        LOGGER.debug("Deleting Booking with bookingId {}", bookingId);

        String hql = "DELETE FROM Booking b WHERE b.bookingId = :bookingId";
        int deletedCount = em.createQuery(hql)
                .setParameter("bookingId", bookingId)
                .executeUpdate();
        return deletedCount > 0;
    }
}
