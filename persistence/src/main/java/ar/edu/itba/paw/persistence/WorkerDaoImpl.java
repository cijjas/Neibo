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

@Repository
public class WorkerDaoImpl implements WorkerDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private UserDao userDao;

        private final String USERS_JOIN_WI =
            "select * \n" +
                    "from users w join workers_info wi on w.userid = wi.workerid ";

    private final String USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI =
            "select *\n" +
                    "from users w join workers_professions wp on w.userid = wp.workerid " +
                    "join professions p on wp.professionid = p.professionid " +
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
        final List<Worker> list = jdbcTemplate.query(USERS_JOIN_WI + " WHERE workerid = ?", ROW_MAPPER, workerId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long neighborhoodId) {
        StringBuilder query = new StringBuilder(USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI);
        List<Object> queryParams = new ArrayList<>();

        query.append(" WHERE 1 = 1");

        query.append(" AND wn.neighborhoodid = ?");
        queryParams.add(neighborhoodId);


        // Append multiple professions conditions
        if (professions != null && !professions.isEmpty()) {
            query.append(" AND EXISTS (");
            query.append("SELECT 1 FROM workers_professions wp JOIN professions p ON wp.professionid = p.professionid");
            query.append(" WHERE wp.workerid = w.userid AND p.profession IN (");
            for (int i = 0; i < professions.size(); i++) {
                query.append("?");
                queryParams.add(professions.get(i)); // Use the profession name as a string
                if (i < professions.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")");
            query.append(" HAVING COUNT(DISTINCT wp.professionid) = ?)"); // Ensure all specified professions exist
            queryParams.add(professions.size());
        }

        if (page != 0) {
            // Calculate the offset based on the page and size
            int offset = (page - 1) * size;
            // Append the LIMIT and OFFSET clauses for pagination
            query.append(" LIMIT ? OFFSET ?");
            queryParams.add(size);
            queryParams.add(offset);
        }

        LOGGER.info(String.valueOf(query));
        LOGGER.info(String.valueOf(queryParams));

        return jdbcTemplate.query(query.toString(), ROW_MAPPER, queryParams.toArray());
    }

    @Override
    public int getWorkersCountByCriteria(List<String> professions, long neighborhoodId) {
        StringBuilder query = new StringBuilder(COUNT_USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI);
        List<Object> queryParams = new ArrayList<>();

        query.append(" WHERE 1 = 1");

        query.append(" AND wn.neighborhoodid = ?");
        queryParams.add(neighborhoodId);

        // Append multiple tags conditions
        if (professions != null && !professions.isEmpty()) {
            query.append(" AND EXISTS (");
            query.append("SELECT 1 FROM workers_professions wp JOIN professions p ON wp.professionid = p.professionid");
            query.append(" WHERE wp.workerid = w.userid AND p.profession IN (");
            for (int i = 0; i < professions.size(); i++) {
                query.append("?");
                queryParams.add(professions.get(i)); // Use the tag name as a string
                if (i < professions.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")");
            query.append(" HAVING COUNT(DISTINCT wp.professionid) = ?)"); // Ensure all specified tags exist
            queryParams.add(professions.size());
        }

        // Log results
        LOGGER.info(String.valueOf(query));
        LOGGER.info(String.valueOf(queryParams));

        return jdbcTemplate.queryForObject(query.toString(), Integer.class, queryParams.toArray());
    }

    // ---------------------------------------------- WORKERS UPDATE -----------------------------------------------------
    @Override
    public void updateWorker(long workerId, String phoneNumber, String address, String businessName, long backgroundPictureId, String bio) {
        try {
            jdbcTemplate.update("UPDATE workers_info SET phoneNumber = ?, address = ?, businessName = ?, backgroundPictureId = ?, bio = ? WHERE workerId = ?",
                    phoneNumber, address, businessName, backgroundPictureId, bio, workerId);
        } catch (DataAccessException ex) {
            LOGGER.error("Error updating the worker", ex);
            throw new InsertionException("An error occurred whilst updating the Worker");
        }
    }
}
