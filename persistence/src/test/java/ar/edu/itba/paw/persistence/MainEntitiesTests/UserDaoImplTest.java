package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Entities.User;
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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class UserDaoImplTest {

    private static final java.sql.Date USER_CREATION_DATE = Date.valueOf("2001-3-14");
    private final String USER_PASSWORD = "password";
    private final String USER_NAME = "John";
    private final String USER_SURNAME = "Doe";
    private final Language USER_LANGUAGE = Language.ENGLISH;
    private final Boolean USER_DARK_MODE = false;
    private final UserRole USER_ROLE = UserRole.NEIGHBOR;
    private final Integer USER_IDENTIFICATION_NUMBER = 12345;
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserDao userDaoImpl;
    private long nhKey1;
    private long nhKey2;
    private long uKey1;
    private long uKey2;
    private long uKey3;
    private long uKey4;

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
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);

        // Exercise
        User user = userDaoImpl.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(user);
        assertEquals(USER_MAIL_1, user.getMail());
        assertEquals(USER_NAME, user.getName());
        assertEquals(USER_SURNAME, user.getSurname());
        assertEquals(USER_LANGUAGE, user.getLanguage());
        assertEquals(USER_DARK_MODE, user.isDarkMode());
        assertEquals(USER_ROLE, user.getRole());
        assertEquals(USER_IDENTIFICATION_NUMBER, user.getIdentification());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_userId_valid() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, USER_CREATION_DATE);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(uKey1);

        // Validations
        assertTrue(optionalUser.isPresent());
        assertEquals(uKey1, optionalUser.get().getUserId().longValue());

    }

    @Test
    public void find_userId_invalid_userId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, USER_CREATION_DATE);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(INVALID_ID);

        // Validations
        assertFalse(optionalUser.isPresent());
    }

    @Test
    public void find_userId_neighborhoodId_valid() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, USER_CREATION_DATE);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(uKey1, nhKey1);

        // Validations
        assertTrue(optionalUser.isPresent());
        assertEquals(uKey1, optionalUser.get().getUserId().longValue());
    }

    @Test
    public void find_userId_neighborhoodId_invalid_userId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, USER_CREATION_DATE);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(INVALID_ID, nhKey1);

        // Validations
        assertFalse(optionalUser.isPresent());
    }

    @Test
    public void find_userId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, USER_CREATION_DATE);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(uKey1, INVALID_ID);

        // Validations
        assertFalse(optionalUser.isPresent());
    }

    @Test
    public void find_userId_neighborhoodId_invalid_userId_neighborhoodId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, USER_CREATION_DATE);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(INVALID_ID, INVALID_ID);

        // Validations
        assertFalse(optionalUser.isPresent());
    }


    @Test
    public void find_email_valid() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(USER_MAIL_1);

        // Validations
        assertTrue(optionalUser.isPresent());
        assertEquals(uKey1, optionalUser.get().getUserId().longValue());
    }

    @Test
    public void find_email_invalid_email() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(INVALID_STRING_ID);

        // Validations
        assertFalse(optionalUser.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void testGetEventsByUser() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey1, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey);

        // Exercise
        List<User> userList = userDaoImpl.getEventUsers(eKey);

        // Validations
        assertEquals(ONE_ELEMENT, userList.size());
    }

    @Test
    public void testGetNoEventsByUser() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey1, tKey1, tKey2);

        // Exercise
        List<User> userList = userDaoImpl.getEventUsers(eKey);

        // Validations
        assertTrue(userList.isEmpty());
    }

    @Test
    public void testGetUsersByNeighborhood() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> userList = userDaoImpl.getUsers(EMPTY_FIELD, nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(TWO_ELEMENTS, userList.size());
    }

    @Test
    public void testGetUsersByNeighborhoodAndRole() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> userList = userDaoImpl.getUsers((long) UserRole.NEIGHBOR.getId(), nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(TWO_ELEMENTS, userList.size());
    }

    @Test
    public void testGetUsersByNeighborhoodAndSize() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> userList = userDaoImpl.getUsers((long) UserRole.NEIGHBOR.getId(), nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(ONE_ELEMENT, userList.size());
    }

    @Test
    public void testGetUsersByNeighborhoodAndSizeAndPage() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> userList = userDaoImpl.getUsers((long) UserRole.NEIGHBOR.getId(), nhKey1, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations
        assertEquals(ONE_ELEMENT, userList.size()); // Adjust based on the expected number of retrieved posts
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
	public void delete_valid() {
	    // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);

	    // Exercise
	    boolean deleted = userDaoImpl.deleteUser(uKey1);

	    // Validations & Post Conditions
		em.flush();
	    assertTrue(deleted);
	    assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users.name()));
	}

	@Test
	public void delete_invalid_userId() {
	    // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);

	    // Exercise
	    boolean deleted = userDaoImpl.deleteUser(INVALID_ID);

	    // Validations & Post Conditions
		em.flush();
	    assertFalse(deleted);
	}

    private void populateUsers() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);

        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.NEIGHBOR, USER_IDENTIFICATION_NUMBER, USER_CREATION_DATE);
        uKey2 = testInserter.createUser(USER_MAIL_2, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.NEIGHBOR, USER_IDENTIFICATION_NUMBER, USER_CREATION_DATE);
        uKey3 = testInserter.createUser(USER_MAIL_3, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey2, USER_LANGUAGE, USER_DARK_MODE, UserRole.UNVERIFIED_NEIGHBOR, USER_IDENTIFICATION_NUMBER, USER_CREATION_DATE);
        uKey4 = testInserter.createUser(USER_MAIL_4, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey2, USER_LANGUAGE, USER_DARK_MODE, UserRole.UNVERIFIED_NEIGHBOR, USER_IDENTIFICATION_NUMBER, USER_CREATION_DATE);
    }
}
