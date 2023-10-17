package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.enums.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class UserDaoImplTest {

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private UserDao userDao;
    private BookingDao bookingDao;
    private ShiftDao shiftDao;
    private AmenityDao amenityDao;
    private DayDao dayDao;
    private TimeDao timeDao;

    private String PASSWORD = "password";
    private String NAME = "John";
    private String SURNAME = "Doe";
    private Language LANGUAGE = Language.ENGLISH;
    private boolean DARK_MODE = false;
    private UserRole ROLE = UserRole.NEIGHBOR;
    private int ID = 12345;

    private static final String NH_NAME_1 = "Neighborhood 1";
    private static final String NH_NAME_2 = "Neighborhood 2";
    private static final String USER_MAIL_1 = "user1@test.com";
    private static final String USER_MAIL_2 = "user2@test.com";
    private static final String USER_MAIL_3 = "user3@test.com";
    private static final String USER_MAIL_4 = "user4@test.com";
    private static final int BASE_PAGE = 1;
    private static final int BASE_PAGE_SIZE = 10;
    private long nhKey1;
    private long nhKey2;
    private long uKey1;
    private long uKey2;
    private long uKey3;
    private long uKey4;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        dayDao = new DayDaoImpl(ds);
        timeDao = new TimeDaoImpl(ds);
        shiftDao = new ShiftDaoImpl(ds, dayDao, timeDao);
        amenityDao = new AmenityDaoImpl(ds, shiftDao);
        bookingDao = new BookingDaoImpl(ds, shiftDao, amenityDao);
        userDao = new UserDaoImpl(ds, bookingDao);
    }

    @Test
    public void testCreateUser() {
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);

        // Exercise
        User createdUser = userDao.createUser(USER_MAIL_1, PASSWORD, NAME, SURNAME, nhKey1, LANGUAGE, DARK_MODE, ROLE, ID);

        // Validations & Post Conditions
        assertNotNull(createdUser);
        assertEquals(USER_MAIL_1, createdUser.getMail());
        assertEquals(NAME, createdUser.getName());
        assertEquals(SURNAME, createdUser.getSurname());
        assertEquals(nhKey1, createdUser.getNeighborhoodId());
        assertEquals(LANGUAGE, createdUser.getLanguage());
        assertEquals(DARK_MODE, createdUser.isDarkMode());
        assertEquals(ROLE, createdUser.getRole());
        assertEquals(ID, createdUser.getIdentification());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users.name()));
    }

    @Test
    public void testFindUserById() {
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, PASSWORD, NAME, SURNAME, nhKey1, LANGUAGE, DARK_MODE, ROLE, ID);

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
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, nhKey1);

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
    public void testSetUserValues(){
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, nhKey1);
        long iKey = testInsertionUtils.createImage();

        // Exercise
        userDao.setUserValues(uKey1,  PASSWORD, NAME, SURNAME,  LANGUAGE, DARK_MODE, iKey,ROLE, ID, nhKey1);

        // Validations
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users.name()));
    }

    /*@Test
    public void testIsAttending() {
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, nhKey1);
        long eId = testInsertionUtils.createEvent(nhKey1);
        testInsertionUtils.createAttendance(uKey1, nhKey1);

        // Exercise
        boolean isAttending = userDao.isAttending(eId, uKey1);

        // Validations
        assertTrue(isAttending);
    }*/

    @Test
    public void testIsNotAttending() {
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, nhKey1);
        long tKey1 = testInsertionUtils.createTime();
        long tKey2 = testInsertionUtils.createTime();
        long eKey = testInsertionUtils.createEvent(nhKey1, tKey1, tKey2);

        // Exercise
        boolean isAttending = userDao.isAttending(eKey, uKey1);

        // Validations
        assertFalse(isAttending);
    }

    /*@Test
    public void testGetEventUsers(){
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, nhKey1);
        long eId = testInsertionUtils.createEvent(nhKey1);
        testInsertionUtils.createAttendance(uKey1, nhKey1);

        // Exercise
        List<User> attendees = userDao.getEventUsers(eId);

        // Validations
        assertFalse(attendees.isEmpty());
        assertEquals(1, attendees.size());
    }*/

    /*@Test
    public void testGetNoEventUsers(){
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, nhKey1);
        long eId = testInsertionUtils.createEvent(nhKey1);

        // Exercise
        List<User> attendees = userDao.getEventUsers(eId);

        // Validations
        assertTrue(attendees.isEmpty());
    }*/

    @Test
    public void testGetNeighborsSubscribedByPostId(){
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, nhKey1);
        long chKey = testInsertionUtils.createChannel();
        long iKey = testInsertionUtils.createImage();
        long pKey = testInsertionUtils.createPost(uKey1, chKey, iKey);
        testInsertionUtils.createSubscription(uKey1, pKey);

        // Exercise
        List<User> subscribers = userDao.getNeighborsSubscribedByPostId(pKey);

        // Validations
        assertFalse(subscribers.isEmpty());
        assertEquals(1, subscribers.size());
    }

    @Test
    public void testGetNoNeighborsSubscribedByPostId(){
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, nhKey1);
        long chKey = testInsertionUtils.createChannel();
        long iKey = testInsertionUtils.createImage();
        long pKey = testInsertionUtils.createPost(uKey1, chKey, iKey);

        // Exercise
        List<User> subscribers = userDao.getNeighborsSubscribedByPostId(pKey);

        // Validations
        assertTrue(subscribers.isEmpty());
    }

    @Test
    public void testGetUsersByNeighborhood(){
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(null, nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(2, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetUsersByNeighborhoodAndRole(){
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(UserRole.NEIGHBOR, nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(2, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetUsersByNeighborhoodAndSize(){
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(UserRole.NEIGHBOR, nhKey1, BASE_PAGE, 1);

        // Validations
        assertEquals(1, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetUsersByNeighborhoodAndSizeAndPage(){
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(UserRole.NEIGHBOR, nhKey1, 2, 1);

        // Validations
        assertEquals(1, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }



    private void populateUsers() {
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        nhKey2 = testInsertionUtils.createNeighborhood(NH_NAME_2);

        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, PASSWORD, NAME, SURNAME, nhKey1, LANGUAGE, DARK_MODE, UserRole.NEIGHBOR, ID);
        uKey2 = testInsertionUtils.createUser(USER_MAIL_2, PASSWORD, NAME, SURNAME, nhKey1, LANGUAGE, DARK_MODE, UserRole.NEIGHBOR, ID);
        uKey3 = testInsertionUtils.createUser(USER_MAIL_3, PASSWORD, NAME, SURNAME, nhKey2, LANGUAGE, DARK_MODE, UserRole.UNVERIFIED_NEIGHBOR, ID);
        uKey4 = testInsertionUtils.createUser(USER_MAIL_4, PASSWORD, NAME, SURNAME, nhKey2, LANGUAGE, DARK_MODE, UserRole.UNVERIFIED_NEIGHBOR, ID);
    }
}
