package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingDaoImpl implements BookingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

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

    private final String BOOKINGS_JOIN_AVAILABILITY =
            "SELECT uav.*, bookingid, date, a.amenityid, s.shiftid, userid, a.name, d.dayid, t.timeid, timeinterval\n" +
                    "FROM users_availability uav\n" +
                    "INNER JOIN amenities_shifts_availability asa ON uav.amenityavailabilityid = asa.amenityavailabilityid\n" +
                    "INNER JOIN amenities a ON asa.amenityid = a.amenityid\n" +
                    "INNER JOIN shifts s ON s.shiftid = asa.shiftid\n" +
                    "INNER JOIN days d ON s.dayid = d.dayid\n" +
                    "INNER JOIN times t ON s.starttime = t.timeid";

    @Override
    public Optional<Booking> findBooking(long bookingId){
        LOGGER.debug("Selecting Booking with id {}", bookingId);
        return Optional.ofNullable(em.find(Booking.class, bookingId));
    }

    @Override
    public List<Booking> getBookings(Long userId) {
        LOGGER.debug("Selecting Bookings from userId {}", userId);

        if (userId == null) {
            String sql = BOOKINGS_JOIN_AVAILABILITY + " ORDER BY uav.date, asa.amenityid, timeinterval asc";
            List<Booking> allBookings = em.createNativeQuery(sql, Booking.class)
                    .getResultList();
            return allBookings;
        } else {
            String sql = BOOKINGS_JOIN_AVAILABILITY + " WHERE userid = :userId ORDER BY uav.date, asa.amenityid, timeinterval asc";
            List<Booking> userBookings = em.createNativeQuery(sql, Booking.class)
                    .setParameter("userId", userId)
                    .getResultList();
            return userBookings;
        }
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
