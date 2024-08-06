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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class WorkerDaoImplTest {


    private final String WORKER_PHONE_NUMBER = "123-456-7890";
    private final String WORKER_ADDRESS = "123 Worker St";
    private final String WORKER_BUSINESS_NAME = "Worker Business";

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private WorkerDaoImpl workerDaoImpl;

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

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Test
    public void create_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL_1, nhKey);

        // Exercise
        Worker worker = workerDaoImpl.createWorker(uKey, WORKER_PHONE_NUMBER, WORKER_ADDRESS, WORKER_BUSINESS_NAME);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(worker);
        assertEquals(uKey, worker.getUser().getUserId().longValue());
        assertEquals(WORKER_PHONE_NUMBER, worker.getPhoneNumber());
        assertEquals(WORKER_ADDRESS, worker.getAddress());
        assertEquals(WORKER_BUSINESS_NAME, worker.getBusinessName());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_info.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_workerId_valid() {
        // Pre Conditions
        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        Optional<Worker> optionalWorker = workerDaoImpl.findWorker(uKey);

        // Validations & Post Conditions
        assertTrue(optionalWorker.isPresent());
        assertEquals(uKey, optionalWorker.get().getUser().getUserId().longValue());
    }

    @Test
    public void find_workerId_invalid_workerId() {
        // Pre Conditions
        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        Optional<Worker> optionalWorker = workerDaoImpl.findWorker(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalWorker.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void testGetWorkersByNeighborhood() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoods = Collections.singletonList(nhKey1);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(BASE_PAGE, BASE_PAGE_SIZE, Collections.emptyList(), neighborhoods, (long) WorkerRole.VERIFIED_WORKER.getId(), EMPTY_FIELD);

        // Validations
        assertEquals(TWO_ELEMENTS, workerList.size());
    }

    @Test
    public void testGetWorkersByNeighborhoodAndProfessions() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.singletonList(pKey1),
                Collections.singletonList(nhKey1),
                (long) WorkerRole.VERIFIED_WORKER.getId(),
                (long) WorkerStatus.NONE.getId());

        // Validations
        assertEquals(ONE_ELEMENT, workerList.size());
    }

    @Test
    public void testGetWorkersByNeighborhoodAndSize() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoods = Collections.singletonList(nhKey1);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(BASE_PAGE, BASE_PAGE_SIZE, Collections.emptyList(), neighborhoods, (long) WorkerRole.VERIFIED_WORKER.getId(), (long) WorkerStatus.NONE.getId());

        // Validations
        assertEquals(ONE_ELEMENT, workerList.size());
    }

    @Test
    public void testGetWorkersByNeighborhoodAndSizeAndPage() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoods = Collections.singletonList(nhKey1);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(TEST_PAGE, TEST_PAGE_SIZE, Collections.emptyList(), neighborhoods, (long) WorkerRole.VERIFIED_WORKER.getId(), (long) WorkerStatus.NONE.getId());

        // Validations
        assertEquals(ONE_ELEMENT, workerList.size());
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

/*
    @Test
	public void testWorkerDelete() {
	    // Pre Conditions
        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

	    // Exercise
	    boolean deleted = workerDao.deleteWorker(uKey);

	    // Validations & Post Conditions
		em.flush();
	    assertTrue(deleted);
	    assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_info.name()));
	}

	@Test
	public void testInvalidWorkerDelete() {
	    // Pre Conditions
        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

	    // Exercise
	    boolean deleted = workerDao.deleteWorker(INVALID_ID);

	    // Validations & Post Conditions
		em.flush();
	    assertFalse(deleted);
	}

*/
    private void populateWorkers() {

        /*
         * 4 Workers
         * Worker 1 -> Neighborhood 1, no profession
         * Worker 2 -> Neighborhood 1, Profession 1
         * Worker 3 -> Neighborhood 2, Profession 2
         * Worker 4 -> Neighborhood 2, Profession 1  & Profession 2
         */

        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);

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
