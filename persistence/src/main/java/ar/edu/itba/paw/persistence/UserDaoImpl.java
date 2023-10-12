package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.User;
import enums.Language;
import enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.paw.persistence.DaoUtils.*;

@Repository
public class UserDaoImpl implements UserDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String USERS =
            "select u.* \n" +
                    "from users u";

    private final String USERS_JOIN_POSTS_USERS_AND_POSTS =
            "select u.*\n" +
                    "from posts p " +
                    "join posts_users_subscriptions on p.postid = posts_users_subscriptions.postid " +
                    "join users u on posts_users_subscriptions.userid = u.userid ";

    private final String EVENTS_JOIN_USERS =
            "select u.* \n" +
                    "from events e " +
                    "join events_users on e.eventid = events_users.eventid " +
                    "join users u on events_users.userid = u.userid ";
    private BookingDao bookingDao;
    private final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> {
        List<Booking> bookings = bookingDao.getUserBookings(rs.getLong("userid"));
        return new User.Builder()
                .userId(rs.getLong("userid"))
                .mail(rs.getString("mail"))
                .name(rs.getString("name"))
                .surname(rs.getString("surname"))
                .password(rs.getString("password"))
                .neighborhoodId(rs.getLong("neighborhoodid"))
                .creationDate(rs.getDate("creationdate"))
                .darkMode(rs.getBoolean("darkmode"))
                .profilePictureId(rs.getLong("profilepictureid"))
                .language(rs.getString("language") != null ? Language.valueOf(rs.getString("language")) : null)
                .role(rs.getString("role") != null ? UserRole.valueOf(rs.getString("role")) : null)
                .bookings(bookings)
                .build();
    };

    // ---------------------------------------------- USERS INSERT -----------------------------------------------------

    @Autowired
    public UserDaoImpl(final DataSource ds, final BookingDao bookingDao) {
        this.bookingDao = bookingDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("userid")
                .withTableName("users");
    }

    // ---------------------------------------------- USERS SELECT -----------------------------------------------------

    @Override
    public User createUser(final String mail, final String password, final String name, final String surname,
                           final long neighborhoodId, final Language language, final boolean darkMode, final UserRole role, final int identification) {
        Map<String, Object> data = new HashMap<>();
        data.put("mail", mail);
        data.put("password", password);
        data.put("name", name);
        data.put("creationDate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("surname", surname);
        data.put("neighborhoodid", neighborhoodId);
        data.put("darkmode", darkMode);
        data.put("language", language != null ? language.toString() : null);
        data.put("role", role != null ? role.toString() : null);
        data.put("identification", identification);
        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new User.Builder()
                    .userId(key.longValue())
                    .name(name).mail(mail)
                    .surname(surname)
                    .password(password)
                    .neighborhoodId(neighborhoodId)
                    .darkMode(darkMode)
                    .language(language)
                    .role(role)
                    .identification(identification)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the User", ex);
            throw new InsertionException("An error occurred whilst creating the User");
        }
    }

    @Override
    public Optional<User> findUserById(final long userId) {
        final List<User> list = jdbcTemplate.query(USERS + " WHERE userid = ?", ROW_MAPPER, userId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<User> findUserByMail(final String mail) {
        final List<User> list = jdbcTemplate.query(USERS + " WHERE mail = ?", ROW_MAPPER, mail);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<User> getNeighborsSubscribedByPostId(long postId) {
        return jdbcTemplate.query(USERS_JOIN_POSTS_USERS_AND_POSTS + " WHERE p.postid = ? AND role = ?", ROW_MAPPER, postId, UserRole.NEIGHBOR.toString());
    }

    @Override
    public List<User> getUsersByCriteria(UserRole role, long neighborhoodId, int page, int size) {

        StringBuilder query = new StringBuilder("SELECT * FROM users u WHERE 1 = 1");
        List<Object> queryParams = new ArrayList<>();

        if (role != null)
            appendRoleCondition(query, queryParams, role);

        if (neighborhoodId > 0)
            appendNeighborhoodIdCondition(query, queryParams, neighborhoodId);

        if (page != 0)
            appendPaginationClause(query, queryParams, page, size);

        return jdbcTemplate.query(query.toString(), ROW_MAPPER, queryParams.toArray());
    }


    public int getTotalUsers(UserRole role, long neighborhoodId) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM users u WHERE 1 = 1");
        List<Object> queryParams = new ArrayList<>();

        if (role != null)
            appendRoleCondition(query, queryParams, role);

        if (neighborhoodId > 0)
            appendNeighborhoodIdCondition(query, queryParams, neighborhoodId);

        // Use queryForObject to retrieve the count as an integer
        return jdbcTemplate.queryForObject(query.toString(), Integer.class, queryParams.toArray());
    }


    // ---------------------------------------------- USERS UPDATE -----------------------------------------------------

    @Override
    public void setUserValues(final long id, final String password, final String name, final String surname,
                              final Language language, final boolean darkMode, final long profilePictureId, final UserRole role, final int identification
    ) {
        jdbcTemplate.update("UPDATE users SET name = ?, surname = ?, password = ?, darkmode = ?, language = ?, role = ?, profilepictureid = ?, identification = ? WHERE userid = ?",
                name, surname, password, darkMode, language != null ? language.toString() : null, role != null ? role.toString() : null, profilePictureId == 0 ? null : profilePictureId, identification, id);
    }

    @Override
    public List<User> getEventUsers(long eventId) {
        return jdbcTemplate.query(EVENTS_JOIN_USERS + " WHERE e.eventid = ?", ROW_MAPPER, eventId);
    }

    @Override
    public boolean isAttending(long eventId, long userId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM events_users WHERE eventid = ? AND userid = ?", Integer.class, eventId, userId) == 1;
    }
}
