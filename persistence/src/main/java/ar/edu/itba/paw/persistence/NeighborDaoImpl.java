package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.NeighborDao;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Neighborhood;
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
public class NeighborDaoImpl implements NeighborDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert; // En vez de hacer queries de tipo INSERT, usamos este objeto.

    private final String baseQuery = "select neighborid, mail, name, surname from neighbors ";

    @Autowired // Motor de inyección de dependencias; nos da el DataSource definido en el @Bean de WebConfig.
    public NeighborDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("neighborid")
                .withTableName("neighbors");
                // con .usingColumns(); podemos especificar las columnas a usar y otras cosas
    }

    @Override
    public Neighbor create(final String mail, final String name, final String surname, final long neighborhoodId) {
        Map<String, Object> data = new HashMap<>();
        data.put("mail", mail);
        data.put("name", name);
        data.put("creationDate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("surname", surname);
        data.put("neighborhoodid", neighborhoodId);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Neighbor.Builder()
                .neighborId(key.longValue())
                .name(name).mail(mail)
                .surname(surname)
                .build();
    }

    private static final RowMapper<Neighbor> ROW_MAPPER = (rs, rowNum) ->
            new Neighbor.Builder()
                    .neighborId(rs.getLong("neighborid"))
                    .mail(rs.getString("mail"))
                    .name(rs.getString("name"))
                    .surname(rs.getString("surname"))
                    .build();

    @Override
    public List<Neighbor> getAllNeighbors() {
        return jdbcTemplate.query("select neighborid, mail, name, surname from neighbors", ROW_MAPPER);
    }

    @Override
    public List<Neighbor> getAllNeighborsByNeighborhood(long neighborhoodId) {
        return jdbcTemplate.query(baseQuery + "where neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }

    @Override
    public Optional<Neighbor> findNeighborById(long id) {
        final List<Neighbor> list = jdbcTemplate.query(baseQuery + "where neighborid = ?", ROW_MAPPER, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<Neighbor> findNeighborByMail(String mail) {
        final List<Neighbor> list = jdbcTemplate.query(baseQuery + "where mail = ?", ROW_MAPPER, mail);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

}
