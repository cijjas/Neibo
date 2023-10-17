package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Worker;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.enums.Table;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class WorkerDaoImplTest {

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;

    private WorkerDao workerDao;
    private UserDao userDao;
    private BookingDao bookingDao;
    private ShiftDao shiftDao;
    private AmenityDao amenityDao;
    private DayDao dayDao;
    private TimeDao timeDao;

    private String PHONE_NUMBER_1 = "123-456-7890";
    private String ADDRESS_1 = "123 Worker St";
    private String BUSINESS_1 = "Worker Business";
    private static final String NH_NAME_1 = "Neighborhood 1";
    private static final String NH_NAME_2 = "Neighborhood 2";
    private static final int BASE_PAGE = 1;
    private static final int BASE_PAGE_SIZE = 10;

    // Attributes to hold the IDs
    private long nhKey1;
    private long nhKey2;
    private long uKey1;
    private long uKey2;
    private long uKey3;
    private long uKey4;
    private long pKey1;
    private long pKey2;

    // Class attributes for emails and profession names
    private String WORKER_MAIL_1 = "worker1-1@test.com";
    private String WORKER_MAIL_2 = "worker2-1@test.com";
    private String WORKER_MAIL_3 = "worker3-2@test.com";
    private String WORKER_MAIL_4 = "worker4-2@test.com";
    private String PROFESSION_1 = "Profession 1";
    private String PROFESSION_2 = "Profession 2";
    private String BIO_1 = "Im alive and in some time ill be dead";


    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        dayDao = new DayDaoImpl(ds);
        timeDao = new TimeDaoImpl(ds);
        shiftDao = new ShiftDaoImpl(ds, dayDao, timeDao);
        amenityDao = new AmenityDaoImpl(ds, shiftDao);
        bookingDao = new BookingDaoImpl(ds, shiftDao, amenityDao);
        userDao = new UserDaoImpl(ds, bookingDao);
        workerDao = new WorkerDaoImpl(ds, userDao);
    }

    @Test
    public void testCreateWorker() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(WORKER_MAIL_1, nhKey);

        // Exercise
        Worker createdWorker = workerDao.createWorker(uKey, PHONE_NUMBER_1, ADDRESS_1, BUSINESS_1);

        // Validations & Post Conditions
        assertNotNull(createdWorker);
        assertEquals(uKey, createdWorker.getUser().getUserId());
        assertEquals(PHONE_NUMBER_1, createdWorker.getPhoneNumber());
        assertEquals(ADDRESS_1, createdWorker.getAddress());
        assertEquals(BUSINESS_1, createdWorker.getBusinessName());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_info.name()));
    }

    @Test
    public void testFindWorkerById() {
        // Pre Conditions
        long pKey = testInsertionUtils.createProfession();
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(WORKER_MAIL_1, nhKey);
        testInsertionUtils.createWorker(uKey);
        testInsertionUtils.createWorkerProfession(uKey, pKey);

        // Exercise
        Optional<Worker> foundWorker = workerDao.findWorkerById(uKey);

        // Validations & Post Conditions
        assertTrue(foundWorker.isPresent());
    }

    @Test
    public void testFindWorkerByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Worker> foundWorker = workerDao.findWorkerById(1);

        // Validations & Post Conditions
        assertFalse(foundWorker.isPresent());
    }

    @Test
    public void testUpdateWorker() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(WORKER_MAIL_1, nhKey);
        long iKey = testInsertionUtils.createImage();
        testInsertionUtils.createWorker(uKey);

        // Exercise
        workerDao.updateWorker(uKey, PHONE_NUMBER_1, ADDRESS_1, BUSINESS_1, iKey, BIO_1);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_info.name()));
    }

    @Test
    public void testGetWorkersByNeighborhood() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> retrievedWorkers = workerDao.getWorkersByCriteria(BASE_PAGE, BASE_PAGE_SIZE, null, nhKey1);

        // Validations
        assertEquals(2, retrievedWorkers.size()); // Adjust based on the expected number of retrieved workers
    }

    @Test
    public void testGetWorkersByNeighborhoodAndProfessions() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> retrievedWorkers = workerDao.getWorkersByCriteria(BASE_PAGE, BASE_PAGE_SIZE, Collections.singletonList(PROFESSION_1), nhKey1);

        // Validations
        assertEquals(1, retrievedWorkers.size()); // Adjust based on the expected number of retrieved workers
    }


    @Test
    public void testGetWorkersByNeighborhoodAndSize() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> retrievedWorkers = workerDao.getWorkersByCriteria(BASE_PAGE, 1, null, nhKey1);

        // Validations
        assertEquals(1, retrievedWorkers.size()); // Adjust based on the expected number of retrieved workers
    }

    @Test
    public void testGetWorkersByNeighborhoodAndSizeAndPage() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> retrievedWorkers = workerDao.getWorkersByCriteria(2, 1, null, nhKey1);

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

        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        nhKey2 = testInsertionUtils.createNeighborhood(NH_NAME_2);

        uKey1 = testInsertionUtils.createUser(WORKER_MAIL_1, nhKey1);
        uKey2 = testInsertionUtils.createUser(WORKER_MAIL_2, nhKey1);
        uKey3 = testInsertionUtils.createUser(WORKER_MAIL_3, nhKey2);
        uKey4 = testInsertionUtils.createUser(WORKER_MAIL_4, nhKey2);

        testInsertionUtils.addWorkerToNeighborhood(uKey1, nhKey1);
        testInsertionUtils.addWorkerToNeighborhood(uKey2, nhKey1);
        testInsertionUtils.addWorkerToNeighborhood(uKey3, nhKey2);
        testInsertionUtils.addWorkerToNeighborhood(uKey4, nhKey2);

        pKey1 = testInsertionUtils.createProfession(PROFESSION_1);
        pKey2 = testInsertionUtils.createProfession(PROFESSION_2);

        testInsertionUtils.createWorker(uKey1);
        testInsertionUtils.createWorker(uKey2);
        testInsertionUtils.createWorkerProfession(uKey2, pKey1);
        testInsertionUtils.createWorker(uKey3);
        testInsertionUtils.createWorkerProfession(uKey3, pKey2);
        testInsertionUtils.createWorker(uKey4);
        testInsertionUtils.createWorkerProfession(uKey4, pKey1);
        testInsertionUtils.createWorkerProfession(uKey4, pKey2);
    }
}
