package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.enums.Table;
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
public class CommentDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private CommentDaoImpl commentDao;

    private UserDao userDao;
    private BookingDao bookingDao;
    private ShiftDao shiftDao;
    private AmenityDao amenityDao;
    private DayDao dayDao;
    private TimeDao timeDao;


    private static final String COMMENT_TEXT = "Sample Comment";
    private static final int BASE_PAGE = 1;
    private static final int BASE_PAGE_SIZE = 10;

    @Autowired
    private DataSource ds;

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
        commentDao = new CommentDaoImpl(ds, userDao);
    }

    @Test
    public void testCreateComment() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long chKey = testInsertionUtils.createChannel();
        long iKey = testInsertionUtils.createImage();
        long pKey = testInsertionUtils.createPost(uKey, chKey, iKey);

        // Exercise
        Comment c = commentDao.createComment(COMMENT_TEXT, uKey, pKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.comments.name()));
        assertEquals(COMMENT_TEXT, c.getComment());
    }

    @Test
    public void testFindCommentById() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long chKey = testInsertionUtils.createChannel();
        long iKey = testInsertionUtils.createImage();
        long pKey = testInsertionUtils.createPost(uKey, chKey, iKey);
        long cKey = testInsertionUtils.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> comment = commentDao.findCommentById(cKey);

        // Validations & Post Conditions
        assertTrue(comment.isPresent());
        assertEquals(cKey, comment.get().getCommentId());
    }

    @Test
    public void testFindCommentByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Comment> comment = commentDao.findCommentById(1);

        // Validations & Post Conditions
        assertFalse(comment.isPresent());
    }

    @Test
    public void testFindCommentsByPostId() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long chKey = testInsertionUtils.createChannel();
        long iKey = testInsertionUtils.createImage();
        long pKey = testInsertionUtils.createPost(uKey, chKey, iKey);
        testInsertionUtils.createComment(uKey, pKey);

        // Exercise
        List<Comment> comments = commentDao.findCommentsByPostId(pKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
    }

    @Test
    public void testFindCommentsByInvalidPostId() {
        // Pre Conditions

        // Exercise
        List<Comment> comments = commentDao.findCommentsByPostId(1, 10, 1);

        // Validations & Post Conditions
        assertTrue(comments.isEmpty());
    }
}
