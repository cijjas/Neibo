package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.models.Entities.Amenity;
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

import static ar.edu.itba.paw.persistence.TestConstants.*;
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

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AmenityDao amenityDaoImpl;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Test
    public void create_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Amenity amenity = amenityDaoImpl.createAmenity(AMENITY_NAME_1, AMENITY_DESCRIPTION_2, nhKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(amenity);
        assertEquals(nhKey, amenity.getNeighborhood().getNeighborhoodId().longValue());
        assertEquals(AMENITY_NAME_1, amenity.getName());
        assertEquals(AMENITY_DESCRIPTION_2, amenity.getDescription());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_amenityId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        Optional<Amenity> optionalAmenity = amenityDaoImpl.findAmenity(aKey);

        // Validations & Post Conditions
        assertTrue(optionalAmenity.isPresent());
        assertEquals(aKey, optionalAmenity.get().getAmenityId().longValue());
    }

    @Test
    public void find_amenityId_invalid_amenityId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        Optional<Amenity> optionalAmenity = amenityDaoImpl.findAmenity(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalAmenity.isPresent());
    }

    @Test
    public void find_amenityId_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        Optional<Amenity> optionalAmenity = amenityDaoImpl.findAmenity(aKey, nhKey);

        // Validations & Post Conditions
        assertTrue(optionalAmenity.isPresent());
        assertEquals(aKey, optionalAmenity.get().getAmenityId().longValue());
    }

    @Test
    public void find_amenityId_neighborhoodId_invalid_amenityId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        Optional<Amenity> optionalAmenity = amenityDaoImpl.findAmenity(INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(optionalAmenity.isPresent());
    }

    @Test
    public void find_amenityId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        Optional<Amenity> optionalAmenity = amenityDaoImpl.findAmenity(aKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalAmenity.isPresent());
    }

    public void find_amenityId_neighborhoodId_invalid_amenityId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        Optional<Amenity> optionalAmenity = amenityDaoImpl.findAmenity(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalAmenity.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        List<Amenity> amenityList = amenityDaoImpl.getAmenities(nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, amenityList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        List<Amenity> amenityList = amenityDaoImpl.getAmenities(nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(amenityList.isEmpty());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

     @Test
    public void count() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        int countAmenities = amenityDaoImpl.countAmenities(nhKey);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countAmenities);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        int countAmenities = amenityDaoImpl.countAmenities(nhKey);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countAmenities);
    }

    @Test
    public void get_pagination() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey1 = testInserter.createAmenity(AMENITY_NAME_1, AMENITY_DESCRIPTION_1, nhKey);
        long aKey2 = testInserter.createAmenity(AMENITY_NAME_2, AMENITY_DESCRIPTION_2, nhKey);

        // Exercise
        List<Amenity> amenityList = amenityDaoImpl.getAmenities(nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, amenityList.size());
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_amenityId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        boolean deleted = amenityDaoImpl.deleteAmenity(aKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities.name()));
    }

    @Test
    public void delete_amenityId_invalid_amenityId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);

        // Exercise
        boolean deleted = amenityDaoImpl.deleteAmenity(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
