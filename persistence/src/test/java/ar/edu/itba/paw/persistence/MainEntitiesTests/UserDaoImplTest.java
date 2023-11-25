package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.MainEntities.Event;
import ar.edu.itba.paw.models.MainEntities.User;
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
public class UserDaoImplTest {

    private static final String NH_NAME_1 = "Neighborhood 1";
    private static final String NH_NAME_2 = "Neighborhood 2";
    private static final String USER_MAIL_1 = "user1@test.com";
    private static final String USER_MAIL_2 = "user2@test.com";
    private static final String USER_MAIL_3 = "user3@test.com";
    private static final String USER_MAIL_4 = "user4@test.com";
    private static final int BASE_PAGE = 1;
    private static final int BASE_PAGE_SIZE = 10;
    private final String PASSWORD = "password";
    private final String NAME = "John";
    private final String SURNAME = "Doe";
    private final Language LANGUAGE = Language.ENGLISH;
    private final Boolean DARK_MODE = false;
    private final UserRole ROLE = UserRole.NEIGHBOR;
    private final Integer ID = 12345;
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserDao userDao;
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

    @Test
    public void testCreateUser() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);

        // Exercise
        User createdUser = userDao.createUser(USER_MAIL_1, PASSWORD, NAME, SURNAME, nhKey1, LANGUAGE, DARK_MODE, ROLE, ID);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(createdUser);
        assertEquals(USER_MAIL_1, createdUser.getMail());
        assertEquals(NAME, createdUser.getName());
        assertEquals(SURNAME, createdUser.getSurname());
        assertEquals(LANGUAGE, createdUser.getLanguage());
        assertEquals(DARK_MODE, createdUser.isDarkMode());
        assertEquals(ROLE, createdUser.getRole());
        assertEquals(ID, createdUser.getIdentification());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users.name()));
    }

    @Test
    public void testFindUserById() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, PASSWORD, NAME, SURNAME, nhKey1, LANGUAGE, DARK_MODE, ROLE, ID);

        // Exercise
        Optional<User> maybeUser = userDao.findUserById(uKey1);

        // Validations
        assertTrue(maybeUser.isPresent());
    }

    @Test
    public void testFindUserByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<User> maybeUser = userDao.findUserById(1);

        // Validations
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testFindUserByMail() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);

        // Exercise
        Optional<User> maybeUser = userDao.findUserByMail(USER_MAIL_1);

        // Validations
        assertTrue(maybeUser.isPresent());
    }

    @Test
    public void testFindUserByInvalidMail() {
        // Pre Conditions

        // Exercise
        Optional<User> maybeUser = userDao.findUserByMail(USER_MAIL_1);

        // Validations
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testIsAttending() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey1, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey);

        // Exercise
        boolean isAttending = userDao.isAttending(eKey, uKey1);

        // Validations
        assertTrue(isAttending);
    }

    @Test
    public void testIsNotAttending() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey1, tKey1, tKey2);

        // Exercise
        boolean isAttending = userDao.isAttending(eKey, uKey1);

        // Validations
        assertFalse(isAttending);
    }

    @Test
    public void testGetEventsByUser() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey1, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey);

        // Exercise
        List<User> events = userDao.getEventUsers(eKey);

        // Validations
        assertEquals(1, events.size());
    }

    @Test
    public void testGetNoEventsByUser() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey1, tKey1, tKey2);

        // Exercise
        List<User> events = userDao.getEventUsers(eKey);

        // Validations
        assertTrue(events.isEmpty());
    }

    @Test
    public void testGetNeighborsSubscribedByPostId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey1, chKey, iKey);
        testInserter.createSubscription(uKey1, pKey);

        // Exercise
        List<User> subscribers = userDao.getNeighborsSubscribedByPostId(pKey);

        // Validations
        assertFalse(subscribers.isEmpty());
        assertEquals(1, subscribers.size());
    }

    @Test
    public void testGetNoNeighborsSubscribedByPostId() {
        // Pre Conditions

        // Exercise
        List<User> subscribers = userDao.getNeighborsSubscribedByPostId(1);

        // Validations
        assertTrue(subscribers.isEmpty());
    }

    @Test
    public void testGetUsersByNeighborhood() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(null, nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(2, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetUsersByNeighborhoodAndRole() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(UserRole.NEIGHBOR, nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(2, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetUsersByNeighborhoodAndSize() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(UserRole.NEIGHBOR, nhKey1, BASE_PAGE, 1);

        // Validations
        assertEquals(1, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetUsersByNeighborhoodAndSizeAndPage() {
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(UserRole.NEIGHBOR, nhKey1, 2, 1);

        // Validations
        assertEquals(1, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }


    private void populateUsers() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);
        nhKey2 = testInserter.createNeighborhood(NH_NAME_2);

        uKey1 = testInserter.createUser(USER_MAIL_1, PASSWORD, NAME, SURNAME, nhKey1, LANGUAGE, DARK_MODE, UserRole.NEIGHBOR, ID);
        uKey2 = testInserter.createUser(USER_MAIL_2, PASSWORD, NAME, SURNAME, nhKey1, LANGUAGE, DARK_MODE, UserRole.NEIGHBOR, ID);
        uKey3 = testInserter.createUser(USER_MAIL_3, PASSWORD, NAME, SURNAME, nhKey2, LANGUAGE, DARK_MODE, UserRole.UNVERIFIED_NEIGHBOR, ID);
        uKey4 = testInserter.createUser(USER_MAIL_4, PASSWORD, NAME, SURNAME, nhKey2, LANGUAGE, DARK_MODE, UserRole.UNVERIFIED_NEIGHBOR, ID);
    }
}
