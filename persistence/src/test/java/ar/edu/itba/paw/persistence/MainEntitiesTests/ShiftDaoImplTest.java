package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.Entities.Shift;
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
public class ShiftDaoImplTest {

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ShiftDao shiftDaoImpl;

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
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();

        // Exercise
        Shift shift = shiftDaoImpl.createShift(dKey, tKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(shift);
        assertEquals(dKey, shift.getDay().getDayId().longValue());
        assertEquals(tKey, shift.getStartTime().getTimeId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.shifts.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_shiftId_valid() {
        // Pre Conditions
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long shiftKey = testInserter.createShift(dKey, tKey);

        // Exercise
        Optional<Shift> optionalShift = shiftDaoImpl.findShift(shiftKey);

        // Validations & Post Conditions
        assertTrue(optionalShift.isPresent());
        assertEquals(shiftKey, optionalShift.get().getShiftId().longValue());
    }

    @Test
    public void find_shiftId_invalid_shiftId() {
        // Pre Conditions
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long shiftKey = testInserter.createShift(dKey, tKey);

        // Exercise
        Optional<Shift> optionalShift = shiftDaoImpl.findShift(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalShift.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey1 = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey,tKey);
        testInserter.createAvailability(aKey1, sKey);

        // Exercise
        List<Shift> shiftList = shiftDaoImpl.getShifts(aKey1);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, shiftList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey1 = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey,tKey);

        // Exercise
        List<Shift> shiftList = shiftDaoImpl.getShifts(aKey1);

        // Validations & Post Conditions
        assertTrue(shiftList.isEmpty());
    }
}
