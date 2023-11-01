package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.MainEntities.Shift;
import ar.edu.itba.paw.models.MainEntities.Time;
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
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ShiftDaoImplTest {

    private final String DATE = "2022-12-12";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ShiftDao shiftDao;

    @PersistenceContext
    private EntityManager em;
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateShift() {
        // Pre Conditions
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();

        // Exercise
        Shift createdShift = shiftDao.createShift(dKey, tKey);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.shifts.name()));
    }

    @Test
    public void testFindShiftById() {
        // Pre Conditions
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long shiftKey = testInserter.createShift(dKey, tKey);

        // Exercise
        Optional<Shift> foundShift = shiftDao.findShiftById(shiftKey);

        // Validations & Post Conditions
        assertTrue(foundShift.isPresent());
    }

    @Test
    public void testFindShiftByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Shift> foundShift = shiftDao.findShiftById(1);

        // Validations & Post Conditions
        assertFalse(foundShift.isPresent());
    }

    @Test
    public void testFindShiftId() {
        // Pre Conditions
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        testInserter.createShift(dKey, tKey);

        // Exercise
        Optional<Shift> foundShift = shiftDao.findShiftId(tKey, dKey);

        // Validations & Post Conditions
        assertTrue(foundShift.isPresent());
    }

    @Test
    public void testFindShiftIdInvalid() {
        // Pre Conditions

        // Exercise
        Optional<Shift> foundShift = shiftDao.findShiftId(1, 1);

        // Validations & Post Conditions
        assertFalse(foundShift.isPresent());
    }

    @Test
    public void testGetShifts() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        testInserter.createAvailability(aKey, sKey);

        // Exercise
        List<Shift> shifts = shiftDao.getShifts(aKey, dKey, Date.valueOf(DATE));

        // Validations & Post Conditions
        assertEquals(1, shifts.size());
    }

    @Test
    public void testGetNoShifts() {
        // Pre Conditions

        // Exercise
        List<Shift> shifts = shiftDao.getShifts(1, 1, Date.valueOf(DATE));

        // Validations & Post Conditions
        assertEquals(0, shifts.size());
    }
}
