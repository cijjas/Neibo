package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import enums.Language;
import enums.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Number nhKey1;
    private Number nhKey2;
    private Number uKey1;
    private Number uKey2;
    private Number uKey3;
    private Number uKey4;

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
        User createdUser = userDao.createUser(USER_MAIL_1, PASSWORD, NAME, SURNAME, nhKey1.longValue(), LANGUAGE, DARK_MODE, ROLE, ID);

        // Validations & Post Conditions
        assertNotNull(createdUser);
        assertEquals(USER_MAIL_1, createdUser.getMail());
        assertEquals(NAME, createdUser.getName());
        assertEquals(SURNAME, createdUser.getSurname());
        assertEquals(nhKey1.longValue(), createdUser.getNeighborhoodId());
        assertEquals(LANGUAGE, createdUser.getLanguage());
        assertEquals(DARK_MODE, createdUser.isDarkMode());
        assertEquals(ROLE, createdUser.getRole());
        assertEquals(ID, createdUser.getIdentification());
    }

    @Test
    public void testFindUserById() {
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        uKey1 = testInsertionUtils.createUser(nhKey1.longValue());

        // Exercise
        Optional<User> maybeUser = userDao.findUserById(uKey1.longValue());

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
        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, nhKey1.longValue());

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
    public void testGetUsersByNeighborhood(){
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(null, nhKey1.longValue(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(2, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetUsersByNeighborhoodAndRole(){
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(UserRole.NEIGHBOR, nhKey1.longValue(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations
        assertEquals(2, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetUsersByNeighborhoodAndSize(){
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(UserRole.NEIGHBOR, nhKey1.longValue(), BASE_PAGE, 1);

        // Validations
        assertEquals(1, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetUsersByNeighborhoodAndSizeAndPage(){
        // Pre Conditions
        populateUsers();

        // Exercise
        List<User> retrievedUsers = userDao.getUsersByCriteria(UserRole.NEIGHBOR, nhKey1.longValue(), 2, 1);

        // Validations
        assertEquals(1, retrievedUsers.size()); // Adjust based on the expected number of retrieved posts
    }

    private void populateUsers() {
        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        nhKey2 = testInsertionUtils.createNeighborhood(NH_NAME_2);

        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, UserRole.NEIGHBOR, nhKey1.longValue());
        uKey1 = testInsertionUtils.createUser(USER_MAIL_2, UserRole.NEIGHBOR, nhKey1.longValue());
        uKey1 = testInsertionUtils.createUser(USER_MAIL_3, UserRole.UNVERIFIED_NEIGHBOR, nhKey2.longValue());
        uKey1 = testInsertionUtils.createUser(USER_MAIL_4, UserRole.UNVERIFIED_NEIGHBOR, nhKey2.longValue());
    }




}
