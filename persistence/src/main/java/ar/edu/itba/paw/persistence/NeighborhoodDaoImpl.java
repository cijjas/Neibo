package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.models.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodDaoImpl.class);
    private static final RowMapper<Neighborhood> ROW_MAPPER = (rs, rowNum) ->
            new Neighborhood.Builder()
                    .neighborhoodId(rs.getLong("neighborhoodid"))
                    .name(rs.getString("neighborhoodname"))
                    .build();
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String NEIGHBORHOODS = "SELECT * FROM neighborhoods ";

    // ----------------------------------------- NEIGHBORHOODS INSERT --------------------------------------------------

    @Autowired
    public NeighborhoodDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("neighborhoodid")
                .withTableName("neighborhoods");
    }

    // ----------------------------------------- NEIGHBORHOODS SELECT --------------------------------------------------

    @Override
    public Neighborhood createNeighborhood(String name) {
        LOGGER.debug("Inserting Neighborhood {}", name);
        Map<String, Object> data = new HashMap<>();
        data.put("neighborhoodname", name);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Neighborhood.Builder()
                    .neighborhoodId(key.longValue())
                    .name(name)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Neighborhood", ex);
            throw new InsertionException("An error occurred whilst creating the Neighborhood");
        }
    }

    @Override
    public Optional<Neighborhood> findNeighborhoodById(long id) {
        LOGGER.debug("Selecting Neighborhood with id {}", id);
        final List<Neighborhood> list = jdbcTemplate.query(NEIGHBORHOODS + " WHERE neighborhoodid = ?", ROW_MAPPER, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<Neighborhood> findNeighborhoodByName(String name) {
        LOGGER.debug("Selecting Neighborhood with name {}", name);
        final List<Neighborhood> list = jdbcTemplate.query(NEIGHBORHOODS + " WHERE neighborhoodname = ?", ROW_MAPPER, name);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Neighborhood> getNeighborhoods() {
        LOGGER.debug("Selecting All Neighborhoods");
        return jdbcTemplate.query(NEIGHBORHOODS, ROW_MAPPER);
    }
}
