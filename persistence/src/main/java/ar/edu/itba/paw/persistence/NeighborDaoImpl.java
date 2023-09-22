package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.NeighborDao;
import ar.edu.itba.paw.models.Neighbor;
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
public class NeighborDaoImpl implements NeighborDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String NEIGHBORS = "select neighborid, mail, password, name, surname, darkmode, language, verified, neighborhoodid from neighbors ";
    private final String NEIGHBORS_JOIN_POSTS_NEIGHBORS_AND_POSTS =
            "select n.neighborid, mail, password, name, surname, darkmode, language, verified, neighborhoodid from\n" +
            "posts p join posts_neighbors on p.postid = posts_neighbors.postid join neighbors n on posts_neighbors.neighborid = n.neighborid ";
    private final String UPDATE_NEIGHBOR ="UPDATE neighbors ";

    @Autowired
    public NeighborDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("neighborid")
                .withTableName("neighbors");
    }

    @Override
    public Neighbor createNeighbor(final String mail, final String password, final String name, final String surname,
                                   final long neighborhoodId, String language, boolean darkMode, boolean verified) {
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

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Neighbor.Builder()
                .neighborId(key.longValue())
                .name(name).mail(mail)
                .surname(surname)
                .password(password)
                .neighborhoodId(neighborhoodId)
                .darkMode(darkMode)
                .language(language)
                .verified(verified)
                .build();
    }

    private static final RowMapper<Neighbor> ROW_MAPPER = (rs, rowNum) ->
            new Neighbor.Builder()
                    .neighborId(rs.getLong("neighborid"))
                    .mail(rs.getString("mail"))
                    .name(rs.getString("name"))
                    .surname(rs.getString("surname"))
                    .password(rs.getString("password"))
                    .neighborhoodId(rs.getLong("neighborhoodid"))
                    .darkMode(rs.getBoolean("darkmode"))
                    .language(rs.getString("language"))
                    .verified(rs.getBoolean("verified"))
                    .build();

    @Override
    public List<Neighbor> getNeighbors() {
        return jdbcTemplate.query(NEIGHBORS, ROW_MAPPER);
    }

    @Override
    public List<Neighbor> getNeighborsByNeighborhood(long neighborhoodId) {
        return jdbcTemplate.query(NEIGHBORS + " where neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }

    @Override
    public List<Neighbor> getNeighborsSubscribedByPostId(long postId) {
        return jdbcTemplate.query(NEIGHBORS_JOIN_POSTS_NEIGHBORS_AND_POSTS + " where p.postid = ?", ROW_MAPPER, postId);
    }
    @Override
    public Optional<Neighbor> findNeighborById(long neighborId) {
        final List<Neighbor> list = jdbcTemplate.query(NEIGHBORS + " where neighborid = ?", ROW_MAPPER, neighborId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<Neighbor> findNeighborByMail(String mail) {
        final List<Neighbor> list = jdbcTemplate.query(NEIGHBORS + " where mail = ?", ROW_MAPPER, mail);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public void updateDarkMode(long id, boolean isDarkMode) {
        jdbcTemplate.update("UPDATE neighbors SET darkmode = ? WHERE neighborid = ?", isDarkMode, id);
    }

    @Override
    public void updateNeighborVerification(long id, boolean isVerified) {
        jdbcTemplate.update("UPDATE neighbors SET verified = ? WHERE neighborid = ?", isVerified, id);
    }

    @Override
    public void updateLanguage(long id, String language) {
        jdbcTemplate.update("UPDATE neighbors SET language = ? WHERE neighborid = ?", language, id);
    }

    @Override
    public void setDefaultValues(long id) {
        jdbcTemplate.update("UPDATE neighbors SET language = 'English', darkmode = false, verified = false  WHERE neighborid = ?", id);
    }

    @Override
    public void setNewPassword(long id, String newPassword){
        jdbcTemplate.update("UPDATE neighbors SET password = ?  WHERE neighborid = ?", newPassword, id);
    }
}
