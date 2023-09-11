package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class NeighborhoodDaoImpl implements NeighborhoodDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert; // En vez de hacer queries de tipo INSERT, usamos este objeto.

    private final String baseQuery = "select neighborhoodid, neighborhoodname from neighborhoods ";

    @Autowired // Motor de inyección de dependencias; nos da el DataSource definido en el @Bean de WebConfig.
    public NeighborhoodDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("neighborhoodid")
                .withTableName("neighborhoods");
        // con .usingColumns(); podemos especificar las columnas a usar y otras cosas
    }

    @Override
    public Neighborhood create(String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("neighborhoodname", name);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Neighborhood.Builder()
                .neighborhoodId(key.longValue())
                .name(name)
                .build();
    }

    private static final RowMapper<Neighborhood> ROW_MAPPER = (rs, rowNum) ->
            new Neighborhood.Builder()
                    .neighborhoodId(rs.getLong("neighborhoodid"))
                    .name(rs.getString("neighborhoodname"))
                    .build();

    @Override
    public List<Neighborhood> getAllNeighborhoods() {
        return jdbcTemplate.query(baseQuery, ROW_MAPPER);
    }

    @Override
    public Optional<Neighborhood> findNeighborhoodById(long id) {
        final List<Neighborhood> list = jdbcTemplate.query(baseQuery + "where neighborhoodid = ?", ROW_MAPPER, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

}
