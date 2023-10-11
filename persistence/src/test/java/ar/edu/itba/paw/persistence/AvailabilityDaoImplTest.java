package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.persistence.config.TestConfig;
import enums.DayOfTheWeek;
import enums.StandardTime;
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
    // Define any necessary constants for your tests here

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

        // Exercise
        //testInsertionUtils.createAvailability();
    }

    @Test
    public void testFindAvailabilityId() {
        // Add your test logic here
    }

    @Test
    public void testFindInvalidAvailabilityId() {
        // Add your test logic here
    }
    // You can add more test methods as needed
}
