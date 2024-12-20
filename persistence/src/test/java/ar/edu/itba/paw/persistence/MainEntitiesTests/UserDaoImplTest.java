package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.UserDaoImpl;
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
public class UserDaoImplTest {

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
    private UserDaoImpl userDaoImpl;
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
        User user = userDaoImpl.createUser(nhKey1, USER_MAIL_1, USER_NAME, USER_SURNAME, USER_PASSWORD, USER_IDENTIFICATION_NUMBER, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE);

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
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, DATE_1);

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
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, DATE_1);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(INVALID_ID);

        // Validations
        assertFalse(optionalUser.isPresent());
    }

    @Test
    public void find_neighborhoodId_userId_valid() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, DATE_1);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(nhKey1, uKey1);

        // Validations
        assertTrue(optionalUser.isPresent());
        assertEquals(uKey1, optionalUser.get().getUserId().longValue());
    }

    @Test
    public void find_neighborhoodId_userId_invalid_userId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, DATE_1);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(nhKey1, INVALID_ID);

        // Validations
        assertFalse(optionalUser.isPresent());
    }

    @Test
    public void find_neighborhoodId_userId_invalid_neighborhoodId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, DATE_1);

        // Exercise
        Optional<User> optionalUser = userDaoImpl.findUser(INVALID_ID, uKey1);

        // Validations
        assertFalse(optionalUser.isPresent());
    }

    @Test
    public void find_neighborhoodId_userId_invalid_userId_neighborhoodId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, USER_ROLE, USER_IDENTIFICATION_NUMBER, DATE_1);

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
    public void getEventUsers() {
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
    public void getEventsUsers_empty() {
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
    public void get() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> userList = userDaoImpl.getUsers(nhKey1, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(SIX_ELEMENTS, userList.size());
    }

    @Test
    public void get_userRoleNeighbor() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> userList = userDaoImpl.getUsers(nhKey1, (long) UserRole.NEIGHBOR.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(THREE_ELEMENTS, userList.size());
    }

    @Test
    public void get_userRoleUnverified() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> userList = userDaoImpl.getUsers(nhKey1, (long) UserRole.UNVERIFIED_NEIGHBOR.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(TWO_ELEMENTS, userList.size());
    }

    @Test
    public void get_userRoleAdministrator() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> userList = userDaoImpl.getUsers(nhKey1, (long) UserRole.ADMINISTRATOR.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(ONE_ELEMENT, userList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<User> userList = userDaoImpl.getUsers(nhKey1, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(NO_ELEMENTS, userList.size());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.NEIGHBOR, USER_IDENTIFICATION_NUMBER, DATE_1);
        uKey4 = testInserter.createUser(USER_MAIL_2, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.NEIGHBOR, USER_IDENTIFICATION_NUMBER, DATE_1);
        uKey4 = testInserter.createUser(USER_MAIL_3, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.NEIGHBOR, USER_IDENTIFICATION_NUMBER, DATE_1);

        // Exercise
        List<User> userList = userDaoImpl.getUsers(nhKey1, EMPTY_FIELD, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations
        assertEquals(ONE_ELEMENT, userList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        populateUsers();

        // Exercise
        int countUser = userDaoImpl.countUsers(nhKey1, EMPTY_FIELD);

        // Validations
        assertEquals(SIX_ELEMENTS, countUser);
    }

    @Test
    public void count_userRoleNeighbor() {
        // Pre Conditions
        populateUsers();

        // Exercise
        int countUser = userDaoImpl.countUsers(nhKey1, (long) UserRole.NEIGHBOR.getId());

        // Validations
        assertEquals(THREE_ELEMENTS, countUser);
    }

    @Test
    public void count_userRoleUnverified() {
        // Pre Conditions
        populateUsers();

        // Exercise
        int countUser = userDaoImpl.countUsers(nhKey1, (long) UserRole.UNVERIFIED_NEIGHBOR.getId());

        // Validations
        assertEquals(TWO_ELEMENTS, countUser);
    }

    @Test
    public void count_userRoleAdministrator() {
        // Pre Conditions
        populateUsers();

        // Exercise
        int countUser = userDaoImpl.countUsers(nhKey1, (long) UserRole.ADMINISTRATOR.getId());

        // Validations
        assertEquals(ONE_ELEMENT, countUser);
    }

    @Test
    public void count_empty() {
        // Pre Conditions

        // Exercise
        int countUser = userDaoImpl.countUsers(nhKey1, EMPTY_FIELD);

        // Validations
        assertEquals(NO_ELEMENTS, countUser);
    }

    // ----------------------------------------------- POPULATION ------------------------------------------------------

    private void populateUsers() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);

        uKey1 = testInserter.createUser(USER_MAIL_1, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.NEIGHBOR, USER_IDENTIFICATION_NUMBER, DATE_1);
        uKey4 = testInserter.createUser(USER_MAIL_2, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.NEIGHBOR, USER_IDENTIFICATION_NUMBER, DATE_1);
        uKey4 = testInserter.createUser(USER_MAIL_3, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.NEIGHBOR, USER_IDENTIFICATION_NUMBER, DATE_1);
        uKey3 = testInserter.createUser(USER_MAIL_4, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.UNVERIFIED_NEIGHBOR, USER_IDENTIFICATION_NUMBER, DATE_1);
        uKey4 = testInserter.createUser(USER_MAIL_5, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.UNVERIFIED_NEIGHBOR, USER_IDENTIFICATION_NUMBER, DATE_1);
        uKey4 = testInserter.createUser(USER_MAIL_6, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey1, USER_LANGUAGE, USER_DARK_MODE, UserRole.ADMINISTRATOR, USER_IDENTIFICATION_NUMBER, DATE_1);
        uKey4 = testInserter.createUser(USER_MAIL_7, USER_PASSWORD, USER_NAME, USER_SURNAME, nhKey2, USER_LANGUAGE, USER_DARK_MODE, UserRole.NEIGHBOR, USER_IDENTIFICATION_NUMBER, DATE_1);
    }
}
