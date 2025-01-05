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
    public static final String DAY_NAME_1 = "DAY 1";
    public static final String DAY_NAME_2 = "DAY 2";
    public static final String DAY_NAME_3 = "DAY 3";
    public static final int TIME_2 = 432432;

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
    public void get() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey1 = testInserter.createAmenity(nhKey);
        long dKey1 = testInserter.createDay(DAY_NAME_1);
        long dKey2 = testInserter.createDay(DAY_NAME_2);
        long dKey3 = testInserter.createDay(DAY_NAME_3);
        long tKey = testInserter.createTime();
        long sKey1 = testInserter.createShift(dKey1, tKey);
        long sKey2 = testInserter.createShift(dKey2, tKey);
        long sKey3 = testInserter.createShift(dKey3, tKey);

        // Exercise
        List<Shift> shiftList = shiftDaoImpl.getShifts(EMPTY_FIELD, NO_DATE, NO_DAY_ID);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, shiftList.size());
    }


    @Test
    public void get_amenityId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey1 = testInserter.createAmenity(nhKey);
        long dKey1 = testInserter.createDay(DAY_NAME_1);
        long dKey2 = testInserter.createDay(DAY_NAME_2);
        long dKey3 = testInserter.createDay(DAY_NAME_3);
        long tKey = testInserter.createTime();
        long sKey1 = testInserter.createShift(dKey1, tKey);
        long sKey2 = testInserter.createShift(dKey2, tKey);
        long sKey3 = testInserter.createShift(dKey3, tKey);
        testInserter.createAvailability(aKey1, sKey1);
        testInserter.createAvailability(aKey1, sKey2);

        // Exercise
        List<Shift> shiftList = shiftDaoImpl.getShifts(aKey1, NO_DATE, NO_DAY_ID);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, shiftList.size());
    }

    @Test
    public void get_amenityId_date() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey1 = testInserter.createAmenity(nhKey);
        int dKey1 = testInserter.createDay(DAY_NAME_1);
        int dKey2 = testInserter.createDay(DAY_NAME_2);
        int dKey3 = testInserter.createDay(DAY_NAME_3);
        long tKey = testInserter.createTime();
        long tKey2 = testInserter.createTime(new java.sql.Time(TIME_2));
        long sKey1 = testInserter.createShift(dKey1, tKey);
        long sKey2 = testInserter.createShift(dKey2, tKey);
        long sKey3 = testInserter.createShift(dKey3, tKey);
        long sKey4 = testInserter.createShift(dKey1, tKey2);
        long avKey1 = testInserter.createAvailability(aKey1, sKey1);
        long avKey2 = testInserter.createAvailability(aKey1, sKey2);
        long avKey3 = testInserter.createAvailability(aKey1, sKey3);
        long avKey4 = testInserter.createAvailability(aKey1, sKey4);
        testInserter.createBooking(uKey, avKey1, DATE_1);

        // Exercise
        List<Shift> shiftList = shiftDaoImpl.getShifts(aKey1, DATE_1, dKey1);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, shiftList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long aKey1 = testInserter.createAmenity(nhKey);
        int dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);

        // Exercise
        List<Shift> shiftList = shiftDaoImpl.getShifts(aKey1, DATE_1, dKey);

        // Validations & Post Conditions
        assertTrue(shiftList.isEmpty());
    }
}
