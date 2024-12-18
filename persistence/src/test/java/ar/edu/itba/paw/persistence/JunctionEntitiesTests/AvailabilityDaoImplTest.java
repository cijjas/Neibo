package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.persistence.JunctionDaos.AvailabilityDaoImpl;
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
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class AvailabilityDaoImplTest {


    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AvailabilityDaoImpl availabilityDaoImpl;

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
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);

        // Exercise
        Availability availability = availabilityDaoImpl.createAvailability(sKey, aKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(availability);
        assertEquals(aKey, availability.getAmenity().getAmenityId().longValue());
        assertEquals(sKey, availability.getShift().getShiftId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities_shifts_availability.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_amenityId_shiftId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        Optional<Availability> optionalAvailability = availabilityDaoImpl.findAvailability(sKey, aKey);

        // Validations & Post Conditions
        assertTrue(optionalAvailability.isPresent());
        assertEquals(avKey, optionalAvailability.get().getAmenityAvailabilityId().longValue());
    }

    @Test
    public void find_amenityId_shiftId_invalid_amenityId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long availabilityKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        Optional<Availability> optionalAvailability = availabilityDaoImpl.findAvailability(sKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalAvailability.isPresent());
    }


    @Test
    public void find_amenityId_shiftId_invalid_shiftId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long availabilityKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        Optional<Availability> optionalAvailability = availabilityDaoImpl.findAvailability(INVALID_ID, aKey);

        // Validations & Post Conditions
        assertFalse(optionalAvailability.isPresent());
    }

    @Test
    public void find_amenityId_shiftId_invalid_amenityId_shiftId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long availabilityKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        Optional<Availability> optionalAvailability = availabilityDaoImpl.findAvailability(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalAvailability.isPresent());
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_amenityId_shiftId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long availabilityKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        boolean deleted = availabilityDaoImpl.deleteAvailability(sKey, aKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities_shifts_availability.name()));
    }

    @Test
    public void delete_amenityId_shiftId_invalid_amenityId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long availabilityKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        boolean deleted = availabilityDaoImpl.deleteAvailability(sKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_amenityId_shiftId_invalid_shiftId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long availabilityKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        boolean deleted = availabilityDaoImpl.deleteAvailability(INVALID_ID, aKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_amenityId_shiftId_invalid_amenityId_shiftId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long availabilityKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        boolean deleted = availabilityDaoImpl.deleteAvailability(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
