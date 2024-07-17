package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.Entities.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.persistence.MainEntitiesDaos.DaoUtils.appendCommonWorkerConditions;
import static ar.edu.itba.paw.persistence.MainEntitiesDaos.DaoUtils.appendPaginationClause;

@Repository
public class WorkerDaoImpl implements WorkerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

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

    // ---------------------------------------------- WORKERS SELECT -----------------------------------------------------

    @Override
    public Optional<Worker> findWorker(long workerId) {
        LOGGER.debug("Selecting Worker with workerId {}", workerId);

        return Optional.ofNullable(em.find(Worker.class, workerId));
    }

    private final String USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI =
            "SELECT wi.workerid, userid, wi.phonenumber, businessname, address, bio, backgroundpictureid\n" +
                    "FROM users w\n" +
                    "LEFT JOIN workers_neighborhoods wn ON w.userid = wn.workerId\n" +
                    "JOIN workers_info wi ON w.userid = wi.workerid\n" +
                    "WHERE w.userid IN (\n" +
                    "    SELECT DISTINCT w.userid\n" +
                    "    FROM users w\n" +
                    "    LEFT JOIN workers_neighborhoods wn ON w.userid = wn.workerid \n";

    private final String COUNT_USERS_JOIN_WP_JOIN_PROFESSIONS_JOIN_WN_JOIN_WI =
            "SELECT COUNT(DISTINCT w.userid)\n" +
                    "FROM users w\n" +
                    "LEFT JOIN workers_professions wp ON w.userid = wp.workerid\n" +
                    "LEFT JOIN professions p ON wp.professionid = p.professionid\n" +
                    "LEFT JOIN workers_neighborhoods wn ON w.userid = wn.workerId\n" +
                    "LEFT JOIN workers_info wi ON w.userid = wi.workerid\n" +
                    "WHERE w.userid IN (\n" +
                    "    SELECT DISTINCT w.userid\n" +
                    "    FROM users w\n" +
                    "    LEFT JOIN workers_neighborhoods wn ON w.userid = wn.workerid ";


    public List<Worker> getWorkers(int page, int size, List<Long> professionIds, List<Long> neighborhoodIds, Long workerRoleId, Long workerStatusId) {
        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("SELECT DISTINCT w.*, wi.* FROM users w ");

        // Join with workers_neighborhoods, workers_info, and other necessary tables
        queryStringBuilder.append("LEFT JOIN workers_neighborhoods wn ON w.userid = wn.workerId ");
        queryStringBuilder.append("JOIN workers_info wi ON w.userid = wi.workerid ");
        queryStringBuilder.append("WHERE 1=1 ");

        // Additional conditions based on optional parameters
        if (workerStatusId != null && !(WorkerStatus.fromId(workerStatusId).equals(WorkerStatus.NONE))) {
            queryStringBuilder.append("AND (SELECT AVG(rating) FROM reviews r WHERE r.workerid = w.userid) > 4 ");
        }

        if (workerRoleId != null) {
            queryStringBuilder.append("AND wn.role = :workerRole ");
        }

        if (professionIds != null && !professionIds.isEmpty()) {
            queryStringBuilder.append("AND EXISTS (SELECT 1 FROM workers_professions wp, professions p ");
            queryStringBuilder.append("WHERE w.userid = wp.workerid AND wp.professionid = p.professionid ");
            queryStringBuilder.append("AND p.profession IN :professions) ");
        }

        if (neighborhoodIds != null && !neighborhoodIds.isEmpty()) {
            queryStringBuilder.append("AND wn.neighborhoodid IN :neighborhoodIds ");
        }

        // Create and execute the native query
        Query nativeQuery = em.createNativeQuery(queryStringBuilder.toString(), Worker.class);
        nativeQuery.setFirstResult((page - 1) * size);
        nativeQuery.setMaxResults(size);

        // Set parameters
        if (workerRoleId != null) {
            nativeQuery.setParameter("workerRole", WorkerRole.fromId(workerRoleId));
        }

        if (professionIds != null && !professionIds.isEmpty()) {
            nativeQuery.setParameter("professions", professionIds);
        }


        if (neighborhoodIds != null && !neighborhoodIds.isEmpty()) {
            nativeQuery.setParameter("neighborhoodIds", neighborhoodIds);
        }

        return nativeQuery.getResultList();
    }

    public int countWorkers(List<Long> professionIds, List<Long> neighborhoodIds, Long workerRoleId, Long workerStatusId) {
        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("SELECT COUNT(DISTINCT w.userid) FROM users w ");

        // Join with workers_neighborhoods, workers_info, and other necessary tables
        queryStringBuilder.append("LEFT JOIN workers_neighborhoods wn ON w.userid = wn.workerId ");
        queryStringBuilder.append("JOIN workers_info wi ON w.userid = wi.workerid ");
        queryStringBuilder.append("WHERE 1=1 ");

        // Additional conditions based on optional parameters
        if (workerStatusId != null  && !(WorkerStatus.fromId(workerStatusId).equals(WorkerStatus.NONE))) {
            queryStringBuilder.append("AND (SELECT AVG(rating) FROM reviews r WHERE r.workerid = w.userid) > 4 ");
        }

        if (workerRoleId != null) {
            queryStringBuilder.append("AND wn.role = :workerRole ");
        }

        if (professionIds != null && !professionIds.isEmpty()) {
            queryStringBuilder.append("AND EXISTS (SELECT 1 FROM workers_professions wp, professions p ");
            queryStringBuilder.append("WHERE w.userid = wp.workerid AND wp.professionid = p.professionid ");
            queryStringBuilder.append("AND p.profession IN :professions) ");
        }

        if (neighborhoodIds != null && !neighborhoodIds.isEmpty()) {
            queryStringBuilder.append("AND wn.neighborhoodid IN :neighborhoodIds ");
        }

        // Create and execute the native query for counting
        Query countQuery = em.createNativeQuery(queryStringBuilder.toString());

        // Set parameters
        if (workerRoleId != null) {
            countQuery.setParameter("workerRole", WorkerRole.fromId(workerRoleId));
        }

        if (professionIds != null && !professionIds.isEmpty()) {
            countQuery.setParameter("professions", professionIds);
        }

        if (neighborhoodIds != null && !neighborhoodIds.isEmpty()) {
            countQuery.setParameter("neighborhoodIds", neighborhoodIds);
        }

        return ((Number) countQuery.getSingleResult()).intValue();
    }

}
