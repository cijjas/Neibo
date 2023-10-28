package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.MainEntities.Time;
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
import java.util.*;

@Repository
public class TimeDaoImpl implements TimeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private static final RowMapper<Time> ROW_MAPPER =
            (rs, rowNum) -> new Time.Builder()
                    .timeId(rs.getLong("timeid"))
                    .timeInterval(rs.getTime("timeinterval"))
                    .build();
    private static final RowMapper<Long> ROW_MAPPER_2 =
            (rs, rowNum) -> rs.getLong("timeid");
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    // ----------------------------------------------- TIMES INSERT ----------------------------------------------------
    private final String TIMES = "SELECT * FROM times ";

    // ----------------------------------------------- TIMES SELECT ----------------------------------------------------

    @Autowired
    public TimeDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("times")
                .usingGeneratedKeyColumns("timeid");
    }

    @Override
    public Time createTime(java.sql.Time timeInterval) {
        LOGGER.debug("Inserting Time {}", timeInterval.getTime());
        Time time =  new Time.Builder()
                .timeInterval(timeInterval)
                .build();
        em.persist(time);
        return time;
    }

    @Override
    public Optional<Time> findTimeById(long timeId) {
        LOGGER.debug("Selecting Time with timeId {}", timeId);
        return Optional.ofNullable(em.find(Time.class, timeId));
    }

    @Override
    public OptionalLong findIdByTime(java.sql.Time time) {
        LOGGER.debug("Selecting Id with time {}", time);
        TypedQuery<Long> query = em.createQuery("SELECT t.id FROM Time t WHERE t.timeInterval = :timeInterval", Long.class);
        query.setParameter("timeInterval", time);
        List<Long> resultList = query.getResultList();
        return resultList.isEmpty() ? OptionalLong.empty() : OptionalLong.of(resultList.get(0));
    }
}
