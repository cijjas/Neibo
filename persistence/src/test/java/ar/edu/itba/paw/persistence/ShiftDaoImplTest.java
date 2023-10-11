package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.Day;
import ar.edu.itba.paw.models.Time;
import ar.edu.itba.paw.models.Shift;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class ShiftDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private ShiftDao shiftDao;
    private DayDao dayDao;
    private TimeDao timeDao;

    private long DAY_ID = 1; // Replace with actual day ID
    private long TIME_ID = 1; // Replace with actual time ID

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        dayDao = new DayDaoImpl(ds);
        timeDao = new TimeDaoImpl(ds);
        shiftDao = new ShiftDaoImpl(ds, dayDao, timeDao);
    }

    @Test
    public void testCreateShift() {
        // Pre Conditions
        Number dKey = testInsertionUtils.createDay();
        Number tKey = testInsertionUtils.createTime();

        // Exercise
        Shift createdShift = shiftDao.createShift(dKey.longValue(), tKey.longValue());

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "shifts"));
    }

    @Test
    public void testFindShiftById() {
        // Pre Conditions
        Number dKey = testInsertionUtils.createDay();
        Number tKey = testInsertionUtils.createTime();
        Number shiftKey = testInsertionUtils.createShift(dKey.longValue(), tKey.longValue());

        // Exercise
        Optional<Shift> foundShift = shiftDao.findShiftById(shiftKey.longValue());

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
        Number dKey = testInsertionUtils.createDay();
        Number tKey = testInsertionUtils.createTime();
        testInsertionUtils.createShift(dKey.longValue(), tKey.longValue());

        // Exercise
        Optional<Shift> foundShift = shiftDao.findShiftId(tKey.longValue(), dKey.longValue());

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
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number aKey = testInsertionUtils.createAmenity(nhKey.longValue());
        Number dKey = testInsertionUtils.createDay();
        Number tKey = testInsertionUtils.createTime();
        Number sKey =  testInsertionUtils.createShift(dKey.longValue(), tKey.longValue());
        testInsertionUtils.createAvailability(aKey.longValue(), sKey.longValue());

        // Exercise
        List<Shift> shifts = shiftDao.getShifts(aKey.longValue(), dKey.longValue(), Date.valueOf("2022-12-12"));

        // Validations & Post Conditions
        assertEquals(1, shifts.size());
    }

    @Test
    public void testGetNoShifts() {
        // Pre Conditions

        // Exercise
        List<Shift> shifts = shiftDao.getShifts(1, 1, Date.valueOf("2022-12-12"));

        // Validations & Post Conditions
        assertEquals(0, shifts.size());
    }
}
