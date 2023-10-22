package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.enums.Table;
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
import java.util.List;
import java.util.Map;

@Repository
public class ProfessionWorkerDaoImpl implements ProfessionWorkerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionWorkerDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String WORKERS_PROFESSIONS_JOIN_PROFESSIONS =
            "SELECT profession " +
                    "FROM workers_professions wp  " +
                    "JOIN professions p ON wp.professionid = p.professionid ";


    @Autowired
    public ProfessionWorkerDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(Table.workers_professions.name());
    }

    // --------------------------------------- WORKERS_PROFESSIONS INSERT ----------------------------------------------
    @Override
    public void addWorkerProfession(long workerId, long professionId) {
        LOGGER.debug("Inserting Worker Profession");
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

    // --------------------------------------- WORKERS_PROFESSIONS SELECT ----------------------------------------------
    @Override
    public List<String> getWorkerProfessions(long workerId) {
        LOGGER.debug("Selecting Professions of Worker {}", workerId);
        return jdbcTemplate.queryForList(
                WORKERS_PROFESSIONS_JOIN_PROFESSIONS +
                        "WHERE workerid = ?",
                new Object[]{workerId},
                String.class
        );
    }
}
