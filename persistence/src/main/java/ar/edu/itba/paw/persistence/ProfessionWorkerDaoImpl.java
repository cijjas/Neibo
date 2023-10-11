package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
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
public class ProfessionWorkerDaoImpl implements ProfessionWorkerDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionWorkerDaoImpl.class);


    @Autowired
    public ProfessionWorkerDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("workers_professions");
    }

    // --------------------------------------- PROFESSIONWORKERS INSERT ------------------------------------------------
    @Override
    public void addWorkerProfession(long workerId, long professionId) {
        Map<String, Object> data = new HashMap<>();
        data.put("workerid", workerId);
        data.put("professionid", professionId);
        try {
            jdbcInsert.execute(data);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Worker Profession", ex);
            throw new InsertionException("An error occurred whilst inserting the Worker Profession");
        }
    }
}
