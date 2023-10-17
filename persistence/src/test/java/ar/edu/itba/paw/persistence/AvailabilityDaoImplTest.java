package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
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
import java.util.OptionalLong;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInsertionUtils.class})
@Sql("classpath:hsqlValueCleanUp.sql")
public class AvailabilityDaoImplTest {


    @Autowired
    private DataSource ds;
    @Autowired
    private TestInsertionUtils testInsertionUtils;

    private JdbcTemplate jdbcTemplate;
    private AmenityDao amenityDao;
    private ShiftDao shiftDao;
    private AvailabilityDao availabilityDao;
    private DayDao dayDao;
    private TimeDao timeDao;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
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
        OptionalLong foundAvailability = availabilityDao.findAvailabilityId(aKey, sKey);

        // Validations & Post Conditions
        assertTrue(foundAvailability.isPresent());
    }

    @Test
    public void testFindInvalidAvailabilityId() {
        // Exercise
        OptionalLong foundAvailability = availabilityDao.findAvailabilityId(1, 1); // Invalid ID

        // Validations & Post Conditions
        assertFalse(foundAvailability.isPresent());
    }

    @Test
    public void testDeleteAvailability() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long aKey = testInsertionUtils.createAmenity(nhKey);
        long dKey = testInsertionUtils.createDay();
        long tKey = testInsertionUtils.createTime();
        long sKey = testInsertionUtils.createShift(dKey, tKey);
        long availabilityKey = testInsertionUtils.createAvailability(aKey, sKey);

        // Exercise
        boolean deleted = availabilityDao.deleteAvailability(aKey, sKey);

        // Validations & Post Conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities_shifts_availability.name()));
    }

    @Test
    public void testDeleteInvalidAvailability() {
        // Pre Conditions

        // Exercise
        boolean deleted = availabilityDao.deleteAvailability(1, 1);

        // Validations & Post Conditions
        assertFalse(deleted);
    }
}
