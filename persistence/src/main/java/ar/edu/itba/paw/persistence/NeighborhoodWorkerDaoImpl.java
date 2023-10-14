package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class NeighborhoodWorkerDaoImpl implements NeighborhoodWorkerDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodWorkerDaoImpl.class);

    @Autowired
    public NeighborhoodWorkerDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("workers_neighborhoods");
    }

    // ----------------------------------------- NEIGHBORHOOD_WORKERS SELECT -------------------------------------------
    @Override
    public void addWorkerToNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.debug("Inserting Worker to a Neighborhood");
        Map<String, Object> data = new HashMap<>();
        data.put("workerid", workerId);
        data.put("neighborhoodid", neighborhoodId);

        try {
            jdbcInsert.execute(data);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the worker to neighborhood", ex);
            throw new InsertionException("An error occurred whilst adding the Worker to the neighborhood");
        }
    }

    // ----------------------------------------- NEIGHBORHOOD_WORKERS DELETE -------------------------------------------
    @Override
    public void removeWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.debug("Deleting Worker {} from Neighborhood {}", workerId, neighborhoodId);
        jdbcTemplate.update("DELETE FROM workers_neighborhoods WHERE workerid = ? AND neighborhoodid = ?", workerId, neighborhoodId);
    }
}
