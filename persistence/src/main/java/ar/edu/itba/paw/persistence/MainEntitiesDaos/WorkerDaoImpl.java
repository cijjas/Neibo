package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.models.JunctionEntities.WorkerArea;
import ar.edu.itba.paw.models.MainEntities.Event;
import ar.edu.itba.paw.models.MainEntities.Post;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.sql.DataSource;
import java.util.*;

import static ar.edu.itba.paw.persistence.MainEntitiesDaos.DaoUtils.*;

@Repository
public class WorkerDaoImpl implements WorkerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String USERS_JOIN_WI =
            "SELECT * \n" +
                    "FROM users w " +
                    "JOIN workers_info wi ON w.userid = wi.workerid ";
    private final String USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI =
            "SELECT w.*, wn.*, wi.* " +
                    "FROM users w " +
                    "JOIN workers_neighborhoods wn ON w.userid = wn.workerId " +
                    "JOIN workers_info wi ON w.userid = wi.workerid " +
                    "WHERE w.userid IN ( " +
                    "SELECT DISTINCT w.userid " +
                    "FROM users w " +
                    "JOIN workers_neighborhoods wn ON w.userid = wn.workerid ";


    private final String COUNT_USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI =
            "SELECT COUNT(distinct w.userid)\n" +
                    "FROM users w " +
                    "JOIN workers_professions wp ON w.userid = wp.workerid " +
                    "JOIN professions p ON wp.professionid = p.professionid " +
                    "JOIN workers_neighborhoods wn ON w.userid = wn.workerId " +
                    "JOIN workers_info wi ON w.userid = wi.workerid ";
    private UserDao userDao;
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

    @Autowired
    public WorkerDaoImpl(final DataSource ds, UserDao userDao) {
        this.userDao = userDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("workers_info");
    }


    // ---------------------------------------------- WORKERS SELECT -----------------------------------------------------

    // ---------------------------------------------- WORKERS INSERT -----------------------------------------------------
    @Override
    public Worker createWorker(long workerId, String phoneNumber, String address, String businessName) {
        LOGGER.debug("Inserting Worker");
        Worker worker = new Worker.Builder()
                .workerId(workerId)
                .user(em.find(User.class, workerId))
                .phoneNumber(phoneNumber)
                .address(address)
                .businessName(businessName)
                .build();
        em.persist(worker);
        return worker;
    }

    @Override
    public Optional<Worker> findWorkerById(long workerId) {
        LOGGER.debug("Selecting Worker with workerId {}", workerId);
        return Optional.ofNullable(em.find(Worker.class, workerId));
    }

    @Override
    public List<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long[] neighborhoodIds, WorkerRole workerRole) {
        LOGGER.debug("Selecting Workers from Neighborhoods {} with professions {}", neighborhoodIds, professions);
        StringBuilder query = new StringBuilder(USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI);
        List<Object> queryParams = new ArrayList<>();

        appendCommonWorkerConditions(query, queryParams, neighborhoodIds, professions, workerRole);
        query.append(") ");

        if (page != 0)
            appendPaginationClause(query, queryParams, page, size);

        // Create a native SQL query
        Query sqlQuery = em.createNativeQuery(query.toString(), Worker.class);

        // Set query parameters
        for (int i = 0; i < queryParams.size(); i++) {
            sqlQuery.setParameter(i + 1, queryParams.get(i));
        }

        // Return the result directly as a list of Worker entities
        return sqlQuery.getResultList();
    }

    @Override
    public int getWorkersCountByCriteria(List<String> professions, long[] neighborhoodIds, WorkerRole workerRole){
        LOGGER.debug("Selecting Workers Count from Neighborhood {} with professions {}", neighborhoodIds, professions);
        StringBuilder query = new StringBuilder(COUNT_USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI);
        List<Object> queryParams = new ArrayList<>();

        appendCommonWorkerConditions(query, queryParams, neighborhoodIds, professions, workerRole);

        // Create a native SQL query for counting
        Query sqlQuery = em.createNativeQuery(query.toString());

        // Set query parameters
        for (int i = 0; i < queryParams.size(); i++) {
            sqlQuery.setParameter(i + 1, queryParams.get(i));
        }

        // Execute the query and retrieve the count
        Object result = sqlQuery.getSingleResult();
        return Integer.parseInt(result.toString());
    }

    // ---------------------------------------------- WORKERS UPDATE -----------------------------------------------------
    @Override
    public void updateWorker(long workerId, String phoneNumber, String address, String businessName, long backgroundPictureId, String bio) {
        LOGGER.debug("Updating Worker {}", workerId);
        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            throw new InsertionException("Worker not found");
        }
        worker.setPhoneNumber(phoneNumber);
        worker.setAddress(address);
        worker.setBusinessName(businessName);
        worker.setBackgroundPictureId(backgroundPictureId);
        worker.setBio(bio);
        em.merge(worker);
    }
}
