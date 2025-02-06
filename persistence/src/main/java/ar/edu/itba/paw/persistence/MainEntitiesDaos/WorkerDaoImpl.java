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
import java.util.List;
import java.util.Optional;

@Repository
public class WorkerDaoImpl implements WorkerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- WORKERS INSERT -----------------------------------------------------

    @Override
    public Worker createWorker(long userId, String phoneNumber, String address, String businessName) {
        LOGGER.debug("Inserting Worker with User Id {}", userId);

        Worker worker = new Worker.Builder()
                .workerId(userId)
                .user(em.find(User.class, userId))
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
        LOGGER.debug("Selecting Worker with Worker Id {}", workerId);

        return Optional.ofNullable(em.find(Worker.class, workerId));
    }

    @Override
    public List<Worker> getWorkers(List<Long> neighborhoodIds, List<Long> professionIds, Long workerRoleId, Long workerStatusId, int page, int size) {
        LOGGER.debug("Selecting Workers affiliated to the Neighborhoods with Neighborhood Ids {}, with Profession Ids {}, Worker Role Id {}, and Worker Status Id {}", neighborhoodIds, professionIds, workerRoleId, workerStatusId);

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
            queryStringBuilder.append("AND (SELECT COUNT(*) FROM workers_professions wp WHERE wp.workerid = w.userid AND wp.professionid IN :professions) = :professionCount ");
        }

        if (neighborhoodIds != null && !neighborhoodIds.isEmpty()) {
            queryStringBuilder.append("AND wn.neighborhoodid IN :neighborhoodIds ");
        }

        // Order by creationDate in the User entity
        queryStringBuilder.append("ORDER BY w.creationDate ASC");

        // Create and execute the native query
        Query nativeQuery = em.createNativeQuery(queryStringBuilder.toString(), Worker.class);
        nativeQuery.setFirstResult((page - 1) * size);
        nativeQuery.setMaxResults(size);

        // Set parameters
        if (workerRoleId != null) {
            nativeQuery.setParameter("workerRole", WorkerRole.fromId(workerRoleId).get().name()); // Optional not empty, check in authorization
        }

        if (professionIds != null && !professionIds.isEmpty()) {
            nativeQuery.setParameter("professions", professionIds);
            nativeQuery.setParameter("professionCount", professionIds.size());
        }

        if (neighborhoodIds != null && !neighborhoodIds.isEmpty()) {
            nativeQuery.setParameter("neighborhoodIds", neighborhoodIds);
        }

        return nativeQuery.getResultList();
    }

    public int countWorkers(List<Long> neighborhoodIds, List<Long> professionIds, Long workerRoleId, Long workerStatusId) {
        LOGGER.debug("Counting Workers affiliated to the Neighborhoods with Neighborhood Ids {}, with Profession Ids {}, Worker Role Id {}, and Worker Status Id {}", neighborhoodIds, professionIds, workerRoleId, workerStatusId);

        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("SELECT COUNT(DISTINCT w.userid) FROM users w ");

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
            queryStringBuilder.append("AND (SELECT COUNT(*) FROM workers_professions wp WHERE wp.workerid = w.userid AND wp.professionid IN :professions) = :professionCount ");
        }

        if (neighborhoodIds != null && !neighborhoodIds.isEmpty()) {
            queryStringBuilder.append("AND wn.neighborhoodid IN :neighborhoodIds ");
        }

        // Create and execute the native query for counting
        Query countQuery = em.createNativeQuery(queryStringBuilder.toString());

        // Set parameters
        if (workerRoleId != null) {
            countQuery.setParameter("workerRole", WorkerRole.fromId(workerRoleId).get().name()); // Optional not empty, check in authorization
        }

        if (professionIds != null && !professionIds.isEmpty()) {
            countQuery.setParameter("professions", professionIds);
            countQuery.setParameter("professionCount", professionIds.size());
        }

        if (neighborhoodIds != null && !neighborhoodIds.isEmpty()) {
            countQuery.setParameter("neighborhoodIds", neighborhoodIds);
        }

        Object result = countQuery.getSingleResult();
        return Integer.parseInt(result.toString());
    }
}
