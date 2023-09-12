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
    private final SimpleJdbcInsert jdbcInsert;

    private final String NEIGHBORHOODS = "select neighborhoodid, neighborhoodname from neighborhoods ";

    @Autowired
    public NeighborhoodDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("neighborhoodid")
                .withTableName("neighborhoods");
    }

    @Override
    public Neighborhood createNeighborhood(String name) {
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
    public List<Neighborhood> getNeighborhoods() {
        return jdbcTemplate.query(NEIGHBORHOODS, ROW_MAPPER);
    }

    @Override
    public Optional<Neighborhood> findNeighborhoodById(long id) {
        final List<Neighborhood> list = jdbcTemplate.query(NEIGHBORHOODS + " where neighborhoodid = ?", ROW_MAPPER, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
