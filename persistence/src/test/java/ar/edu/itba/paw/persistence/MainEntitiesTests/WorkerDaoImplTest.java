package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.WorkerDaoImpl;
import ar.edu.itba.paw.persistence.TestInserter;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class WorkerDaoImplTest {

    private static final String NH_NAME_1 = "Neighborhood 1";
    private static final String NH_NAME_2 = "Neighborhood 2";
    private static final int BASE_PAGE = 1;
    private static final int BASE_PAGE_SIZE = 10;
    public static final String NEW_PHONE = "New Phone";
    public static final String NEW_ADDRESS = "New Address";
    public static final String NEW_BUSINESS = "New Business";
    public static final String NEW_BIO = "New Bio";
    private final String PHONE_NUMBER_1 = "123-456-7890";
    private final String ADDRESS_1 = "123 Worker St";
    private final String BUSINESS_1 = "Worker Business";
    private final String WORKER_MAIL_1 = "worker1-1@test.com";
    private final String WORKER_MAIL_2 = "worker2-1@test.com";
    private final String WORKER_MAIL_3 = "worker3-2@test.com";
    private final String WORKER_MAIL_4 = "worker4-2@test.com";
    private final String PROFESSION_1 = "Profession 1";
    private final String PROFESSION_2 = "Profession 2";
    private final String BIO_1 = "Im alive and in some time ill be dead";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private WorkerDaoImpl workerDao;

    @PersistenceContext
    private EntityManager em;
    private long nhKey1;
    private long nhKey2;
    private long uKey1;
    private long uKey2;
    private long uKey3;
    private long uKey4;
    private long pKey1;
    private long pKey2;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateWorker() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL_1, nhKey);

        // Exercise
        Worker createdWorker = workerDao.createWorker(uKey, PHONE_NUMBER_1, ADDRESS_1, BUSINESS_1);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(createdWorker);
        assertEquals(uKey, createdWorker.getUser().getUserId().longValue());
        assertEquals(PHONE_NUMBER_1, createdWorker.getPhoneNumber());
        assertEquals(ADDRESS_1, createdWorker.getAddress());
        assertEquals(BUSINESS_1, createdWorker.getBusinessName());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_info.name()));
    }

    @Test
    public void testFindWorkerById() {
        // Pre Conditions
        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        Optional<Worker> foundWorker = workerDao.findWorker(uKey);

        // Validations & Post Conditions
        assertTrue(foundWorker.isPresent());
    }

    @Test
    public void testFindWorkerByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Worker> foundWorker = workerDao.findWorker(1);

        // Validations & Post Conditions
        assertFalse(foundWorker.isPresent());
    }

    @Test
    public void testGetWorkersByNeighborhood() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoods = Collections.singletonList(nhKey1);

        // Exercise
        List<Worker> retrievedWorkers = workerDao.getWorkers(BASE_PAGE, BASE_PAGE_SIZE, null, neighborhoods, WorkerRole.VERIFIED_WORKER.name(), WorkerStatus.NONE.name());

        // Validations
        assertEquals(2, retrievedWorkers.size()); // Adjust based on the expected number of retrieved workers
    }

    @Test
    public void testGetWorkersByNeighborhoodAndProfessions() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoods = Collections.singletonList(nhKey1);

        // Exercise
        List<Worker> retrievedWorkers = workerDao.getWorkers(BASE_PAGE, BASE_PAGE_SIZE, Collections.singletonList(Professions.PLUMBER.name()), neighborhoods, WorkerRole.VERIFIED_WORKER.name(), WorkerStatus.NONE.name());

        // Validations
        assertEquals(1, retrievedWorkers.size()); // Adjust based on the expected number of retrieved workers
    }

    @Test
    public void testGetWorkersByNeighborhoodAndSize() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoods = Collections.singletonList(nhKey1);

        // Exercise
        List<Worker> retrievedWorkers = workerDao.getWorkers(BASE_PAGE, 1, null, neighborhoods, WorkerRole.VERIFIED_WORKER.name(), WorkerStatus.NONE.name());

        // Validations
        assertEquals(1, retrievedWorkers.size()); // Adjust based on the expected number of retrieved workers
    }

    @Test
    public void testGetWorkersByNeighborhoodAndSizeAndPage() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoods = Collections.singletonList(nhKey1);

        // Exercise
        List<Worker> retrievedWorkers = workerDao.getWorkers(2, 1, null, neighborhoods, WorkerRole.VERIFIED_WORKER.name(), WorkerStatus.NONE.name());

        // Validations
        assertEquals(1, retrievedWorkers.size()); // Adjust based on the expected number of retrieved workers
    }

    private void populateWorkers() {

        /*
         * 4 Workers
         * Worker 1 -> Neighborhood 1, no profession
         * Worker 2 -> Neighborhood 1, Profession 1
         * Worker 3 -> Neighborhood 2, Profession 2
         * Worker 4 -> Neighborhood 2, Profession 1  & Profession 2
         */

        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);
        nhKey2 = testInserter.createNeighborhood(NH_NAME_2);

        uKey1 = testInserter.createUser(WORKER_MAIL_1, nhKey1);
        uKey2 = testInserter.createUser(WORKER_MAIL_2, nhKey1);
        uKey3 = testInserter.createUser(WORKER_MAIL_3, nhKey2);
        uKey4 = testInserter.createUser(WORKER_MAIL_4, nhKey2);

        pKey1 = testInserter.createProfession(Professions.PLUMBER);
        pKey2 = testInserter.createProfession(Professions.CARPENTER);

        testInserter.createWorker(uKey1);
        testInserter.createWorker(uKey2);
        testInserter.createSpecialization(uKey2, pKey1);
        testInserter.createWorker(uKey3);
        testInserter.createSpecialization(uKey3, pKey2);
        testInserter.createWorker(uKey4);
        testInserter.createSpecialization(uKey4, pKey1);
        testInserter.createSpecialization(uKey4, pKey2);

        testInserter.createAffiliation(uKey1, nhKey1);
        testInserter.createAffiliation(uKey2, nhKey1);
        testInserter.createAffiliation(uKey3, nhKey2);
        testInserter.createAffiliation(uKey4, nhKey2);
    }
}
