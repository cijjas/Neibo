package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Worker;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
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
    private Number nhKey1;
    private Number nhKey2;
    private Number uKey1;
    private Number uKey2;
    private Number uKey3;
    private Number uKey4;
    private Number pKey1;
    private Number pKey2;

    // Class attributes for emails and profession names
    private static final String WORKER_MAIL_1 = "worker1-1@test.com";
    private static final String WORKER_MAIL_2 = "worker2-1@test.com";
    private static final String WORKER_MAIL_3 = "worker3-2@test.com";
    private static final String WORKER_MAIL_4 = "worker4-2@test.com";
    private static final String PROFESSION_1 = "Profession 1";
    private static final String PROFESSION_2 = "Profession 2";

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
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());

        // Exercise
        Worker createdWorker = workerDao.createWorker(uKey.longValue(), PHONE_NUMBER_1, ADDRESS_1, BUSINESS_1);

        // Validations & Post Conditions
        assertNotNull(createdWorker);
        assertEquals(uKey.longValue(), createdWorker.getUser().getUserId());
        assertEquals(PHONE_NUMBER_1, createdWorker.getPhoneNumber());
        assertEquals(ADDRESS_1, createdWorker.getAddress());
        assertEquals(BUSINESS_1, createdWorker.getBusinessName());
    }

    @Test
    public void testFindWorkerById() {
        // Pre Conditions
        Number pKey = testInsertionUtils.createProfession();
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        testInsertionUtils.createWorker(uKey.longValue());
        testInsertionUtils.createWorkerProfession(uKey.longValue(), pKey.longValue());

        // Exercise
        Optional<Worker> foundWorker = workerDao.findWorkerById(uKey.longValue());

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

/*
    @Test
    public void testGetWorkersByCriteriaNeighborhood() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> workers = workerDao.getWorkersByCriteria(1, 10, null, nhKey1.longValue());

        // Validations & Post Conditions
        assertEquals(2, workers.size());
    }

    @Test
    public void testGetWorkersByCriteriaNeighborhoodAndProfession() {
        // Pre Conditions
        populateWorkers();

        // Exercise
        List<Worker> workers = workerDao.getWorkersByCriteria(1, 10, PROFESSION_1, nhKey1.longValue());

        // Validations & Post Conditions
        assertEquals(2, workers.size());
    }
*/

    @Test
    public void testUpdateWorker() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        testInsertionUtils.createWorker(uKey.longValue());

        // Exercise
        String newPhoneNumber = "987-654-3210";
        String newAddress = "321 Worker Ave";
        String newBusinessName = "New Business Name";
        workerDao.updateWorker(uKey.longValue(), newPhoneNumber, newAddress, newBusinessName, 0, null);

        // Validations & Post Conditions
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

        uKey1 = testInsertionUtils.createUser(WORKER_MAIL_1, nhKey1.longValue());
        uKey2 = testInsertionUtils.createUser(WORKER_MAIL_2, nhKey1.longValue());
        uKey3 = testInsertionUtils.createUser(WORKER_MAIL_3, nhKey2.longValue());
        uKey4 = testInsertionUtils.createUser(WORKER_MAIL_4, nhKey2.longValue());

        pKey1 = testInsertionUtils.createProfession(PROFESSION_1);
        pKey2 = testInsertionUtils.createProfession(PROFESSION_2);

        testInsertionUtils.createWorker(uKey1.longValue());
        testInsertionUtils.createWorker(uKey2.longValue());
        testInsertionUtils.createWorkerProfession(uKey2.longValue(), pKey1.longValue());
        testInsertionUtils.createWorker(uKey3.longValue());
        testInsertionUtils.createWorkerProfession(uKey3.longValue(), pKey2.longValue());
        testInsertionUtils.createWorker(uKey4.longValue());
        testInsertionUtils.createWorkerProfession(uKey4.longValue(), pKey1.longValue());
        testInsertionUtils.createWorkerProfession(uKey4.longValue(), pKey2.longValue());

    }
}
