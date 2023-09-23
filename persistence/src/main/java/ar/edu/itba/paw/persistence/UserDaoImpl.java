package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String USERS =
            "select userid, mail, password, name, surname, darkmode, language, verified, neighborhoodid, role \n" +
            "from users" ;
    private final String USERS_JOIN_NEIGHBORHOODS =
            "select userid, mail, password, name, surname, darkmode, language, verified, u.neighborhoodid, role\n" +
                    "from users u join neighborhoods nh on u.neighborhoodid = nh.neighborhoodid ";
    private final String USERS_JOIN_POSTS_USERS_AND_POSTS =
            "select u.userid, mail, password, name, surname, darkmode, language, verified, neighborhoodid, role\n" +
            "from posts p join posts_users on p.postid = posts_users.postid join users u on posts_users.userid = u.userid ";

    @Autowired
    public UserDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("userid")
                .withTableName("users");
    }

    @Override
    public User createUser(final String mail, final String password, final String name, final String surname,
                           final long neighborhoodId, final String language, final boolean darkMode, final boolean verified, final String role) {
        Map<String, Object> data = new HashMap<>();
        data.put("mail", mail);
        data.put("password", password);
        data.put("name", name);
        data.put("creationDate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("surname", surname);
        data.put("neighborhoodid", neighborhoodId);
        data.put("darkmode", darkMode);
        data.put("language", language);
        data.put("verified", verified);
        data.put("role", role);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new User.Builder()
                .userId(key.longValue())
                .name(name).mail(mail)
                .surname(surname)
                .password(password)
                .neighborhoodId(neighborhoodId)
                .darkMode(darkMode)
                .language(language)
                .verified(verified)
                .role(role)
                .build();
    }

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) ->
            new User.Builder()
                    .userId(rs.getLong("userid"))
                    .mail(rs.getString("mail"))
                    .name(rs.getString("name"))
                    .surname(rs.getString("surname"))
                    .password(rs.getString("password"))
                    .neighborhoodId(rs.getLong("neighborhoodid"))
                    .darkMode(rs.getBoolean("darkmode"))
                    .language(rs.getString("language"))
                    .verified(rs.getBoolean("verified"))
                    .role(rs.getString("role"))
                    .build();

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query(USERS, ROW_MAPPER);
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
    public void setUserValues(final long id, final String name, final String surname, final String password, final boolean darkMode, final String language, final boolean verified, final String role) {
        jdbcTemplate.update("UPDATE users SET name = ?, surname = ?, password = ?, darkmode = ?, language = ?, verified = ?, role = ?  WHERE userid = ?", name, surname, password, darkMode, language, verified, role, id);
    }


    // --------------------- Neighbor methods

    @Override
    public User createNeighbor(final String mail, final String password, final String name, final String surname,
                               final long neighborhoodId, final String language, final boolean darkMode, final boolean verified) {
        return createUser(mail, password, name, surname, neighborhoodId ,language ,darkMode ,verified , "Neighbor");
    }

    @Override
    public List<User> getNeighbors() {
        return jdbcTemplate.query(USERS + "WHERE role LIKE 'Neighbor'", ROW_MAPPER);
    }

    @Override
    public List<User> getNeighborsByNeighborhood(long neighborhoodId) {
        return jdbcTemplate.query(USERS + " WHERE neighborhoodid = ? AND role LIKE 'Neighbor'", ROW_MAPPER, neighborhoodId);
    }

    @Override
    public List<User> getNeighborsSubscribedByPostId(long postId) {
        return jdbcTemplate.query(USERS_JOIN_POSTS_USERS_AND_POSTS + " WHERE p.postid = ? AND role LIKE 'Neighbor'", ROW_MAPPER, postId);
    }

    @Override
    public List<User> getNeighborsByNeighborhoodByVerification(long neighborhoodId, boolean verification){
        return jdbcTemplate.query(USERS_JOIN_NEIGHBORHOODS + " WHERE n.neighborhoodid = ? AND n.verified = ? AND role LIKE 'Neighbor'", ROW_MAPPER, neighborhoodId, verification);
    }

    @Override
    public Optional<User> findNeighborById(long userid) {
        final List<User> list = jdbcTemplate.query(USERS + " WHERE userid = ? AND role LIKE 'Neighbor'", ROW_MAPPER, userid);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<User> findNeighborByMail(String mail) {
        final List<User> list = jdbcTemplate.query(USERS + " WHERE mail = ? AND role LIKE 'Neighbor'", ROW_MAPPER, mail);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // ------------------------------



}
