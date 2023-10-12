package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.persistence.config.TestConfig;
import enums.Table;
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
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class AvailabilityDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private AmenityDao amenityDao;
    private ShiftDao shiftDao;
    private AvailabilityDao availabilityDao;
    private DayDao dayDao;
    private TimeDao timeDao;

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        dayDao = new DayDaoImpl(ds);
        timeDao = new TimeDaoImpl(ds);
        shiftDao = new ShiftDaoImpl(ds, dayDao, timeDao);
        amenityDao = new AmenityDaoImpl(ds, shiftDao);
        availabilityDao = new AvailabilityDaoImpl(ds, amenityDao, shiftDao);
    }

    @Test
    public void testCreateAvailability() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long aKey = testInsertionUtils.createAmenity(nhKey);
        long dKey = testInsertionUtils.createDay();
        long tKey = testInsertionUtils.createTime();
        long sKey = testInsertionUtils.createShift(dKey, tKey);

        // Exercise
        Number createdAvailability = availabilityDao.createAvailability(aKey, sKey);

        // Validations & Post Conditions
        assertNotNull(createdAvailability);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities_shifts_availability.name()));
    }

    @Test
    public void testFindAvailabilityId() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long aKey = testInsertionUtils.createAmenity(nhKey);
        long dKey = testInsertionUtils.createDay();
        long tKey = testInsertionUtils.createTime();
        long sKey = testInsertionUtils.createShift(dKey, tKey);
        long availabilityKey = testInsertionUtils.createAvailability(aKey, sKey);

        // Exercise
        Optional<Long> foundAvailability = availabilityDao.findAvailabilityId(aKey, sKey);

        // Validations & Post Conditions
        assertTrue(foundAvailability.isPresent());
    }

    @Test
    public void testFindInvalidAvailabilityId() {
        // Exercise
        Optional<Long> foundAvailability = availabilityDao.findAvailabilityId(1, 1); // Invalid ID

        // Validations & Post Conditions
        assertFalse(foundAvailability.isPresent());
    }
}
