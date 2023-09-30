package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.User;
import enums.Language;
import enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String USERS =
            "select u.* \n" +
                    "from users u";

    private final String USERS_JOIN_POSTS_USERS_AND_POSTS =
            "select u.*\n" +
                    "from posts p join posts_users on p.postid = posts_users.postid join users u on posts_users.userid = u.userid ";

    @Autowired
    public UserDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("userid")
                .withTableName("users");
    }

    // ---------------------------------------------- USERS INSERT -----------------------------------------------------

    @Override
    public User createUser(final String mail, final String password, final String name, final String surname,
                           final long neighborhoodId, final Language language, final boolean darkMode, final UserRole role) {
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
                .build();
    }

    // ---------------------------------------------- USERS SELECT -----------------------------------------------------

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) ->
            new User.Builder()
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
                    .build();

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

        StringBuilder query = new StringBuilder("SELECT * FROM users WHERE 1 = 1");
        List<Object> queryParams = new ArrayList<>();

        if (role != null) {
            query.append(" AND role = ?");
            queryParams.add(role.toString());
        }

        if (neighborhoodId > 0) {
            query.append(" AND neighborhoodid = ?");
            queryParams.add(neighborhoodId);
        }

        if ( page != 0 ){
            int offset = (page - 1) * size;

            query.append(" LIMIT ? OFFSET ?");
            queryParams.add(size);
            queryParams.add(offset);
        }


        return jdbcTemplate.query(query.toString(), ROW_MAPPER, queryParams.toArray());
    }


    public int getTotalUsers(UserRole role, long neighborhoodId) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1 = 1");
        List<Object> queryParams = new ArrayList<>();

        if (role != null) {
            query.append(" AND role = ?");
            queryParams.add(role.toString());
        }

        if (neighborhoodId > 0) {
            query.append(" AND neighborhoodid = ?");
            queryParams.add(neighborhoodId);
        }

        // Use queryForObject to retrieve the count as an integer
        return jdbcTemplate.queryForObject(query.toString(), Integer.class, queryParams.toArray());
    }


    // ---------------------------------------------- USERS UPDATE -----------------------------------------------------

    @Override
    public void setUserValues(final long id, final String password, final String name, final String surname, final Language language, final boolean darkMode, final long profilePictureId,final UserRole role) {
        jdbcTemplate.update("UPDATE users SET name = ?, surname = ?, password = ?, darkmode = ?, language = ?, role = ?, profilepictureid = ?  WHERE userid = ?",
                name, surname, password, darkMode, language != null ? language.toString() : null, role != null ? role.toString() : null, profilePictureId == 0 ? null : profilePictureId, id);
    }
}
