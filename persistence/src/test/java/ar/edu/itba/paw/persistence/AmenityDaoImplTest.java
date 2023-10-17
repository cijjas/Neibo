package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.Amenity;
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
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class AmenityDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private ShiftDao shiftDao;
    private AmenityDao amenityDao;
    private DayDao dayDao;
    private TimeDao timeDao;

    private String AMENITY_NAME = "Amenity Name";
    private String AMENITY_DESCRIPTION = "Amenity Description";

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
    }

    @Test
    public void testCreateAmenity() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();

        // Exercise
        Amenity createdAmenity = amenityDao.createAmenity(AMENITY_NAME, AMENITY_DESCRIPTION, nhKey);

        // Validations & Post Conditions
        assertNotNull(createdAmenity);
        assertEquals(AMENITY_NAME, createdAmenity.getName());
        assertEquals(AMENITY_DESCRIPTION, createdAmenity.getDescription());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities.name()));
    }

    @Test
    public void testFindAmenityById() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long aKey = testInsertionUtils.createAmenity(nhKey);

        // Exercise
        Optional<Amenity> foundAmenity = amenityDao.findAmenityById(aKey);

        // Validations & Post Conditions
        assertTrue(foundAmenity.isPresent());
    }

    @Test
    public void testFindAmenityByInvalidId() {
        // Exercise
        Optional<Amenity> foundAmenity = amenityDao.findAmenityById(1);

        // Validations & Post Conditions
        assertFalse(foundAmenity.isPresent());
    }

    @Test
    public void testGetAmenities() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long aKey = testInsertionUtils.createAmenity(nhKey);

        // Exercise
        List<Amenity> amenities = amenityDao.getAmenities(nhKey);

        // Validations & Post Conditions
        assertEquals(1, amenities.size());
    }
}
