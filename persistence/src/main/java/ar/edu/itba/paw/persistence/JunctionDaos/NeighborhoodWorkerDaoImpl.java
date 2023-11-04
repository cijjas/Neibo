package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.compositeKeys.ChannelMappingKey;
import ar.edu.itba.paw.compositeKeys.WorkerAreaKey;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.models.JunctionEntities.ChannelMapping;
import ar.edu.itba.paw.models.JunctionEntities.WorkerArea;
import ar.edu.itba.paw.models.MainEntities.Channel;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;
import ar.edu.itba.paw.models.MainEntities.User;
import ar.edu.itba.paw.models.MainEntities.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class NeighborhoodWorkerDaoImpl implements NeighborhoodWorkerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodWorkerDaoImpl.class);

    @PersistenceContext
    private EntityManager em;
    // --------------------------------------- NIEGHBORHOODWORKERS SELECT ------------------------------------------
    private static final RowMapper<Neighborhood> ROW_MAPPER = (rs, rowNum) ->
            new Neighborhood.Builder()
                    .neighborhoodId(rs.getLong("neighborhoodid"))
                    .name(rs.getString("neighborhoodname"))
                    .build();
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String NEIGHBORHOODS_JOIN_WORKERS_NEIGHBORHOODS =
            "SELECT * FROM workers_neighborhoods wn JOIN neighborhoods n ON wn.neighborhoodId = n.neighborhoodId";

    @Autowired
    public NeighborhoodWorkerDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("workers_neighborhoods");
    }

    // ----------------------------------------- NEIGHBORHOOD_WORKERS INSERT -------------------------------------------
    @Override
    public WorkerArea createWorkerArea(long workerId, long neighborhoodId) {
        LOGGER.debug("Inserting Worker {} to Neighborhood {}", workerId, neighborhoodId);
        WorkerArea workerArea = new WorkerArea(em.find(Worker.class, workerId), em.find(Neighborhood.class, neighborhoodId));
        em.persist(workerArea);
        return workerArea;
    }

    @Override
    public List<Neighborhood> getNeighborhoods(long workerId) {
        LOGGER.debug("Selecting neighborhoods for the Worker {}", workerId);
        TypedQuery<Neighborhood> query = em.createQuery("SELECT n FROM Worker w JOIN w.workNeighborhoods n WHERE w.user.id = :workerId", Neighborhood.class);
        query.setParameter("workerId", workerId);
        return query.getResultList();
    }

    // ----------------------------------------- NEIGHBORHOOD_WORKERS DELETE -------------------------------------------
    @Override
    public boolean deleteWorkerArea(long workerId, long neighborhoodId) {
        LOGGER.debug("Deleting Worker {} from Neighborhood {}", workerId, neighborhoodId);
        WorkerArea workerArea = em.find(WorkerArea.class, new WorkerAreaKey(workerId, neighborhoodId));
        if (workerArea != null) {
            em.remove(workerArea);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setNeighborhoodRole(long workerId, WorkerRole role, long neighborhoodId) {
        LOGGER.debug("Setting Worker {} role to {} in Neighborhood {}", workerId, role, neighborhoodId);
        WorkerArea workerArea = em.find(WorkerArea.class, new WorkerAreaKey(workerId, neighborhoodId));
        if (workerArea != null) {
            workerArea.setRole(role);
        }
    }
}
