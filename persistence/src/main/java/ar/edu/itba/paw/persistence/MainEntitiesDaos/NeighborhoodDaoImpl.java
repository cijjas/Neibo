package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class NeighborhoodDaoImpl implements NeighborhoodDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
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
        Neighborhood neighborhood = new Neighborhood.Builder()
                .name(name)
                .build();
        em.persist(neighborhood);
        return neighborhood;
    }

    @Override
    public Optional<Neighborhood> findNeighborhoodById(long id) {
        LOGGER.debug("Selecting Neighborhood with id {}", id);
        return Optional.ofNullable(em.find(Neighborhood.class,  id));
    }

    @Override
    public Optional<Neighborhood> findNeighborhoodByName(String name) {
        LOGGER.debug("Selecting Neighborhood with name {}", name);
        TypedQuery<Neighborhood> query = em.createQuery("FROM Neighborhood WHERE neighborhoodname = :neighborhoodName", Neighborhood.class);
        query.setParameter("neighborhoodName", name);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Neighborhood> getNeighborhoods() {
        LOGGER.debug("Selecting All Neighborhoods");
        String jpql = "SELECT n FROM Neighborhood n";
        TypedQuery<Neighborhood> query = em.createQuery(jpql, Neighborhood.class);
        return query.getResultList();
    }
}
