package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.JunctionEntities.Availability;
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
import java.util.OptionalLong;

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
    private AvailabilityDao availabilityDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateAvailability() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);

        // Exercise
        Availability createdAvailability = availabilityDao.createAvailability(aKey, sKey);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.amenities_shifts_availability.name()));
    }

    @Test
    public void testFindAvailabilityId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long availabilityKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        OptionalLong foundAvailability = availabilityDao.findAvailabilityId(aKey, sKey);

        // Validations & Post Conditions
        assertTrue(foundAvailability.isPresent());
    }

    @Test
    public void testFindInvalidAvailabilityId() {
        // Exercise
        OptionalLong foundAvailability = availabilityDao.findAvailabilityId(1L, 1L); // Invalid ID

        // Validations & Post Conditions
        assertFalse(foundAvailability.isPresent());
    }

    @Test
    public void testDeleteAvailability() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long availabilityKey = testInserter.createAvailability(aKey, sKey);

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
