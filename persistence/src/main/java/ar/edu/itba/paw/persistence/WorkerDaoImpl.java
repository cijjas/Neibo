package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.models.Worker;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

import static ar.edu.itba.paw.persistence.DaoUtils.appendCommonWorkerConditions;
import static ar.edu.itba.paw.persistence.DaoUtils.appendPaginationClause;

@Repository
public class WorkerDaoImpl implements WorkerDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private UserDao userDao;

        private final String USERS_JOIN_WI =
            "select * \n" +
                    "from users w join workers_info wi on w.userid = wi.workerid ";

    private final String USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI =
            "select distinct w.*, wn.*, wi.* " +
                "from users w " +
                "join workers_neighborhoods wn on w.userid = wn.workerId " +
                "join workers_info wi on w.userid = wi.workerid ";


    private final String COUNT_USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI =
            "select count(distinct w.userid)\n" +
                    "from users w join workers_professions wp on w.userid = wp.workerid " +
                    "join professions p on wp.professionid = p.professionid " +
                    "join workers_neighborhoods wn on w.userid = wn.workerId " +
                    "join workers_info wi on w.userid = wi.workerid ";

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerDaoImpl.class);

    @Autowired
    public WorkerDaoImpl(final DataSource ds, UserDao userDao) {
        this.userDao = userDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("workers_info");
    }
    // ---------------------------------------------- WORKERS INSERT -----------------------------------------------------
    @Override
    public Worker createWorker(long workerId, String phoneNumber, String address, String businessName) {
        LOGGER.info("Inserting Worker");
        Map<String, Object> data = new HashMap<>();
        data.put("workerId", workerId);
        data.put("phoneNumber", phoneNumber);
        data.put("address", address);
        data.put("businessName", businessName);

        try {
            jdbcInsert.execute(data);
            Optional<User> optionalUser = userDao.findUserById(workerId);
            return optionalUser.map(user -> new Worker.Builder()
                    .user(user)
                    .phoneNumber(phoneNumber)
                    .address(address)
                    .businessName(businessName)
                    .build()).orElse(null);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the worker", ex);
            throw new InsertionException("An error occurred whilst creating the Worker");
        }
    }


    // ---------------------------------------------- WORKERS SELECT -----------------------------------------------------

    private final RowMapper<Worker> ROW_MAPPER = (rs, rowNum) -> {
        User user = userDao.findUserById(rs.getLong("workerid")).orElse(null);
        return new Worker.Builder()
                .user(user)
                .phoneNumber(rs.getString("phoneNumber"))
                .address(rs.getString("address"))
                .businessName(rs.getString("businessName"))
                .backgroundPictureId(rs.getLong("backgroundPictureId"))
                .bio(rs.getString("bio"))
                .build();
    };

    @Override
    public Optional<Worker> findWorkerById(long workerId) {
        LOGGER.info("Selecting Worker with workerId {}", workerId);
        final List<Worker> list = jdbcTemplate.query(USERS_JOIN_WI + " WHERE workerid = ?", ROW_MAPPER, workerId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long neighborhoodId) {
        LOGGER.info("Selecting Workers from Neighborhood {} with professions {}", neighborhoodId, professions);
        StringBuilder query = new StringBuilder(USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI);
        List<Object> queryParams = new ArrayList<>();

        appendCommonWorkerConditions(query, queryParams, neighborhoodId, professions);

        if(page != 0)
            appendPaginationClause(query, queryParams, page, size);

        LOGGER.info(String.valueOf(query));
        LOGGER.info(String.valueOf(queryParams));

        return jdbcTemplate.query(query.toString(), ROW_MAPPER, queryParams.toArray());
    }

    @Override
    public int getWorkersCountByCriteria(List<String> professions, long neighborhoodId) {
        LOGGER.info("Selecting Workers Count from Neighborhood {} with professions {}", neighborhoodId, professions);
        StringBuilder query = new StringBuilder(COUNT_USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI);
        List<Object> queryParams = new ArrayList<>();

        appendCommonWorkerConditions(query, queryParams, neighborhoodId, professions);

        LOGGER.info(String.valueOf(query));
        LOGGER.info(String.valueOf(queryParams));

        return jdbcTemplate.queryForObject(query.toString(), Integer.class, queryParams.toArray());
    }

    // ---------------------------------------------- WORKERS UPDATE -----------------------------------------------------
    @Override
    public void updateWorker(long workerId, String phoneNumber, String address, String businessName, long backgroundPictureId, String bio) {
        LOGGER.info("Updating Worker {}", workerId);
        try {
            jdbcTemplate.update("UPDATE workers_info SET phoneNumber = ?, address = ?, businessName = ?, backgroundPictureId = ?, bio = ? WHERE workerId = ?",
                    phoneNumber, address, businessName, backgroundPictureId, bio, workerId);
        } catch (DataAccessException ex) {
            LOGGER.error("Error updating the worker", ex);
            throw new InsertionException("An error occurred whilst updating the Worker");
        }
    }
}
