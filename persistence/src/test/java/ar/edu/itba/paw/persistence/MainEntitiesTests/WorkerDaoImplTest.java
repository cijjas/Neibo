package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.UserDaoImpl;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private long nhKey3;
    private long nhKey4;
    private long uKey1;
    private long uKey2;
    private long uKey3;
    private long uKey4;
    private long pKey1;
    private long pKey2;
    @Autowired
    private UserDaoImpl userDaoImpl;

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
    public void get() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                Collections.emptyList(),
                EMPTY_FIELD,
                EMPTY_FIELD);

        // Validations
        assertEquals(FOUR_ELEMENTS, workerList.size());
    }

    @Test
    public void get_professionIds() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey1);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                professionList,
                Collections.emptyList(),
                EMPTY_FIELD,
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, workerList.size());
    }

    @Test
    public void get_neighborhoodIds() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey3);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                neighborhoodList,
                EMPTY_FIELD,
                EMPTY_FIELD
        );

        // Validations
        assertEquals(FOUR_ELEMENTS, workerList.size());
    }

    @Test
    public void get_verifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                Collections.emptyList(),
                (long) WorkerRole.VERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(THREE_ELEMENTS, workerList.size());
    }

    @Test
    public void get_unverifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                Collections.emptyList(),
                (long) WorkerRole.UNVERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, workerList.size());
    }

    @Test
    public void get_rejectedWorkerRole() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                Collections.emptyList(),
                (long) WorkerRole.REJECTED.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(THREE_ELEMENTS, workerList.size());
    }

    @Test
    public void get_professionIds_neighborhoodIds() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey3);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                professionList,
                neighborhoodList,
                EMPTY_FIELD,
                EMPTY_FIELD
        );

        // Validations
        assertEquals(THREE_ELEMENTS, workerList.size());
    }

    @Test
    public void get_professionIds_verifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                professionList,
                Collections.emptyList(),
                (long) WorkerRole.VERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, workerList.size());
    }

    @Test
    public void get_professionIds_unverifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                professionList,
                Collections.emptyList(),
                (long) WorkerRole.UNVERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, workerList.size());
    }

    @Test
    public void get_professionIds_rejectedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                professionList,
                Collections.emptyList(),
                (long) WorkerRole.REJECTED.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, workerList.size());
    }

    @Test
    public void get_neighborhoodIds_verifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey2);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                neighborhoodList,
                (long) WorkerRole.VERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(THREE_ELEMENTS, workerList.size());
    }

    @Test
    public void get_neighborhoodIds_unverifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey3);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                neighborhoodList,
                (long) WorkerRole.UNVERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, workerList.size());
    }

    @Test
    public void get_neighborhoodIds_rejectedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey2);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                neighborhoodList,
                (long) WorkerRole.REJECTED.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(ONE_ELEMENT, workerList.size());
    }

    @Test
    public void get_professionIds_neighborhoodIds_verifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey2);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                professionList,
                neighborhoodList,
                (long) WorkerRole.VERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, workerList.size());
    }

    @Test
    public void get_professionIds_neighborhoodIds_unverifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey3);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                professionList,
                neighborhoodList,
                (long) WorkerRole.UNVERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, workerList.size());
    }

    @Test
    public void get_professionIds_neighborhoodIds_rejectedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey2);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                professionList,
                neighborhoodList,
                (long) WorkerRole.REJECTED.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(ONE_ELEMENT, workerList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                Collections.emptyList(),
                EMPTY_FIELD,
                EMPTY_FIELD);

        // Validations
        assertTrue(workerList.isEmpty());
    }

    @Test
    public void get_pagination() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1); // Workers Neighborhood
        nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        nhKey3 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_3);
        nhKey4 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_4);
        uKey1 = testInserter.createUser(WORKER_MAIL_1, nhKey1);
        uKey2 = testInserter.createUser(WORKER_MAIL_2, nhKey1);
        uKey3 = testInserter.createUser(WORKER_MAIL_3, nhKey1);
        uKey4 = testInserter.createUser(WORKER_MAIL_4, nhKey1);
        pKey1 = testInserter.createProfession(Professions.PLUMBER.name());
        pKey2 = testInserter.createProfession(Professions.CARPENTER.name());
        testInserter.createWorker(uKey1);
        testInserter.createWorker(uKey2);
        testInserter.createWorker(uKey3);

        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                TEST_PAGE,
                TEST_PAGE_SIZE,
                Collections.emptyList(),
                Collections.emptyList(),
                EMPTY_FIELD,
                EMPTY_FIELD);

        // Validations
        assertEquals(ONE_ELEMENT, workerList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                Collections.emptyList(),
                Collections.emptyList(),
                EMPTY_FIELD,
                EMPTY_FIELD);

        // Validations
        assertEquals(FOUR_ELEMENTS, countWorker);
    }

    @Test
    public void count_professionIds() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey1);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                professionList,
                Collections.emptyList(),
                EMPTY_FIELD,
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, countWorker);
    }

    @Test
    public void count_neighborhoodIds() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey3);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                Collections.emptyList(),
                neighborhoodList,
                EMPTY_FIELD,
                EMPTY_FIELD
        );

        // Validations
        assertEquals(FOUR_ELEMENTS, countWorker);
    }

    @Test
    public void count_verifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                Collections.emptyList(),
                Collections.emptyList(),
                (long) WorkerRole.VERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(THREE_ELEMENTS, countWorker);
    }

    @Test
    public void count_unverifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                Collections.emptyList(),
                Collections.emptyList(),
                (long) WorkerRole.UNVERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, countWorker);
    }

    @Test
    public void count_rejectedWorkerRole() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                Collections.emptyList(),
                Collections.emptyList(),
                (long) WorkerRole.REJECTED.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(THREE_ELEMENTS, countWorker);
    }

    @Test
    public void count_professionIds_neighborhoodIds() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey3);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                professionList,
                neighborhoodList,
                EMPTY_FIELD,
                EMPTY_FIELD
        );

        // Validations
        assertEquals(THREE_ELEMENTS, countWorker);
    }

    @Test
    public void count_professionIds_verifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                professionList,
                Collections.emptyList(),
                (long) WorkerRole.VERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, countWorker);
    }

    @Test
    public void count_professionIds_unverifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                professionList,
                Collections.emptyList(),
                (long) WorkerRole.UNVERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, countWorker);
    }

    @Test
    public void count_professionIds_rejectedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                professionList,
                Collections.emptyList(),
                (long) WorkerRole.REJECTED.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, countWorker);
    }

    @Test
    public void count_neighborhoodIds_verifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey2);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                Collections.emptyList(),
                neighborhoodList,
                (long) WorkerRole.VERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(THREE_ELEMENTS, countWorker);
    }

    @Test
    public void count_neighborhoodIds_unverifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey3);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                Collections.emptyList(),
                neighborhoodList,
                (long) WorkerRole.UNVERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, countWorker);
    }

    @Test
    public void count_neighborhoodIds_rejectedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey2);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                Collections.emptyList(),
                neighborhoodList,
                (long) WorkerRole.REJECTED.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(ONE_ELEMENT, countWorker);
    }

    @Test
    public void count_professionIds_neighborhoodIds_verifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey2);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                professionList,
                neighborhoodList,
                (long) WorkerRole.VERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, countWorker);
    }

    @Test
    public void count_professionIds_neighborhoodIds_unverifiedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey3);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                professionList,
                neighborhoodList,
                (long) WorkerRole.UNVERIFIED_WORKER.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(TWO_ELEMENTS, countWorker);
    }

    @Test
    public void count_professionIds_neighborhoodIds_rejectedWorkerRole() {
        // Pre Conditions
        populateWorkers();
        List<Long> professionList = new ArrayList<>();
        professionList.add(pKey2);
        List<Long> neighborhoodList = new ArrayList<>();
        neighborhoodList.add(nhKey2);

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                professionList,
                neighborhoodList,
                (long) WorkerRole.REJECTED.getId(),
                EMPTY_FIELD
        );

        // Validations
        assertEquals(ONE_ELEMENT, countWorker);
    }

    @Test
    public void count_empty() {
        // Pre Conditions

        // Exercise
        int countWorker = workerDaoImpl.countWorkers(
                Collections.emptyList(),
                Collections.emptyList(),
                EMPTY_FIELD,
                EMPTY_FIELD);

        // Validations
        assertEquals(NO_ELEMENTS, countWorker);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
	public void delete_valid() {
	    // Pre Conditions
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_info.name()));

        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_info.name()));
        // Exercise
        List<Worker> workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                Collections.emptyList(),
                EMPTY_FIELD,
                EMPTY_FIELD);
        System.out.println(workerList);
	    // Exercise
        boolean deleted = workerDaoImpl.deleteWorker(uKey);
        // boolean deleted = userDaoImpl.deleteUser(uKey);

        workerList = workerDaoImpl.getWorkers(
                BASE_PAGE,
                BASE_PAGE_SIZE,
                Collections.emptyList(),
                Collections.emptyList(),
                EMPTY_FIELD,
                EMPTY_FIELD);
        System.out.println(workerList);
	    // Validations & Post Conditions
		em.flush();
	    assertTrue(deleted);
	    assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_info.name()));
	}

	@Test
	public void delete_invalid_workerId() {
	    // Pre Conditions
        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

	    // Exercise
	    boolean deleted = workerDaoImpl.deleteWorker(INVALID_ID);

	    // Validations & Post Conditions
		em.flush();
	    assertFalse(deleted);
	}


    // ----------------------------------------------- POPULATION ------------------------------------------------------

    private void populateWorkers() {

        // Professions, Neighborhoods, Role(VERIFIED, UNVERIFIED, REJECTED)

        // [{P1, P2}, {N2(VERIFIED), N3(UNVERIFIED)},
        // [{P1}, {N2(VERIFIED)},
        // [{P2}, {N2(VERIFIED), N4(REJECTED)},
        // [{P2}, {N2(REJECTED), N3(VERIFIED)},

        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1); // Workers Neighborhood
        nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        nhKey3 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_3);
        nhKey4 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_4);

        uKey1 = testInserter.createUser(WORKER_MAIL_1, nhKey1);
        uKey2 = testInserter.createUser(WORKER_MAIL_2, nhKey1);
        uKey3 = testInserter.createUser(WORKER_MAIL_3, nhKey1);
        uKey4 = testInserter.createUser(WORKER_MAIL_4, nhKey1);

        pKey1 = testInserter.createProfession(Professions.PLUMBER.name());
        pKey2 = testInserter.createProfession(Professions.CARPENTER.name());

        testInserter.createWorker(uKey1);
        testInserter.createWorker(uKey2);
        testInserter.createWorker(uKey3);
        testInserter.createWorker(uKey4);

        testInserter.createSpecialization(uKey1, pKey1);
        testInserter.createSpecialization(uKey1, pKey2);
        testInserter.createSpecialization(uKey2, pKey1);
        testInserter.createSpecialization(uKey3, pKey2);
        testInserter.createSpecialization(uKey4, pKey2);

        testInserter.createAffiliation(uKey1, nhKey2, WorkerRole.VERIFIED_WORKER);
        testInserter.createAffiliation(uKey2, nhKey2, WorkerRole.VERIFIED_WORKER);
        testInserter.createAffiliation(uKey3, nhKey2, WorkerRole.VERIFIED_WORKER);
        testInserter.createAffiliation(uKey4, nhKey2, WorkerRole.REJECTED);

        testInserter.createAffiliation(uKey1, nhKey3, WorkerRole.UNVERIFIED_WORKER);
        testInserter.createAffiliation(uKey2, nhKey3, WorkerRole.VERIFIED_WORKER);
        testInserter.createAffiliation(uKey3, nhKey3, WorkerRole.VERIFIED_WORKER);
        testInserter.createAffiliation(uKey4, nhKey3, WorkerRole.UNVERIFIED_WORKER);

        testInserter.createAffiliation(uKey1, nhKey4, WorkerRole.UNVERIFIED_WORKER);
        testInserter.createAffiliation(uKey2, nhKey4, WorkerRole.REJECTED);
        testInserter.createAffiliation(uKey3, nhKey4, WorkerRole.REJECTED);
        testInserter.createAffiliation(uKey4, nhKey4, WorkerRole.REJECTED);
    }
}
