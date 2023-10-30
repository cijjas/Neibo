package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.models.MainEntities.Amenity;
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
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class AmenityDaoImplTest {

    private final String AMENITY_NAME_1 = "Amenity Name";
    private final String AMENITY_NAME_2 = "Amenity Name 2";
    private final String AMENITY_DESCRIPTION_1 = "Amenity Description";
    private final String AMENITY_DESCRIPTION_2 = "Amenity Description 2";
    private static final int BASE_PAGE = 1;
    private static final int BASE_PAGE_SIZE = 10;
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AmenityDao amenityDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateAmenity() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Amenity createdAmenity = amenityDao.createAmenity(AMENITY_NAME_1, AMENITY_DESCRIPTION_2, nhKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(createdAmenity);
        assertEquals(AMENITY_NAME_1, createdAmenity.getName());
        assertEquals(AMENITY_DESCRIPTION_2, createdAmenity.getDescription());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities.name()));
    }

    @Test
    public void testFindAmenityById() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        Optional<Amenity> foundAmenity = amenityDao.findAmenityById(aKey);

        // Validations & Post Conditions
        assertTrue(foundAmenity.isPresent());
    }

    @Test
    public void testFindAmenityByInvalidId() {
        // Exercise
        Optional<Amenity> foundAmenity = amenityDao.findAmenityById(1L);

        // Validations & Post Conditions
        assertFalse(foundAmenity.isPresent());
    }

    @Test
    public void testGetAmenities() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        List<Amenity> amenities = amenityDao.getAmenities(nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(1, amenities.size());
    }

    @Test
    public void testGetAmenitiesBySize() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey1 = testInserter.createAmenity(AMENITY_NAME_1, AMENITY_DESCRIPTION_1, nhKey);
        long aKey2 = testInserter.createAmenity(AMENITY_NAME_2, AMENITY_DESCRIPTION_2, nhKey);

        // Exercise
        List<Amenity> amenities = amenityDao.getAmenities(nhKey, BASE_PAGE, 1);

        // Validations & Post Conditions
        assertEquals(1, amenities.size());
    }

    @Test
    public void testGetNoAmenities() {
        // Pre Conditions

        // Exercise
        List<Amenity> amenities = amenityDao.getAmenities(0, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(amenities.isEmpty());
    }

    @Test
    public void testDeleteAmenity() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        boolean deleted = amenityDao.deleteAmenity(aKey);

        // Validations & Post Conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities.name()));
    }

    @Test
    public void testDeleteInvalidAmenity() {
        // Pre Conditions

        // Exercise
        boolean deleted = amenityDao.deleteAmenity(1L);

        // Validations & Post Conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities.name()));
    }
}
