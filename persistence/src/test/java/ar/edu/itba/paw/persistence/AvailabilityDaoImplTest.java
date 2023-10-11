package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.Availability;
import ar.edu.itba.paw.models.Neighborhood;
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
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number aKey = testInsertionUtils.createAmenity(nhKey.longValue());
        Number dKey = testInsertionUtils.createDay();
        Number tKey = testInsertionUtils.createTime();
        Number sKey = testInsertionUtils.createShift(dKey.longValue(), tKey.longValue());

        // Exercise
        Number createdAvailability = availabilityDao.createAvailability(aKey.longValue(), sKey.longValue());

        // Validations & Post Conditions
        assertNotNull(createdAvailability);
    }

    @Test
    public void testFindAvailabilityId() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number aKey = testInsertionUtils.createAmenity(nhKey.longValue());
        Number dKey = testInsertionUtils.createDay();
        Number tKey = testInsertionUtils.createTime();
        Number sKey = testInsertionUtils.createShift(dKey.longValue(), tKey.longValue());
        Number availabilityKey = testInsertionUtils.createAvailability(aKey.longValue(), sKey.longValue());

        // Exercise
        Optional<Long> foundAvailability = availabilityDao.findAvailabilityId(aKey.longValue(), sKey.longValue());

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
