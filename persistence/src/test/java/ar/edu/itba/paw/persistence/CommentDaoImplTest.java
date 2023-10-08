package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.CommentDaoImpl;
import ar.edu.itba.paw.persistence.Table;
import ar.edu.itba.paw.persistence.TestInsertionUtils;
import ar.edu.itba.paw.persistence.UserDaoImpl;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    private static final String COMMENT_TEXT = "Sample Comment";

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        userDao = new UserDaoImpl(ds);
        commentDao = new CommentDaoImpl(ds, userDao);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateComment() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        Number chKey = testInsertionUtils.createChannel();
        Number pKey = testInsertionUtils.createPost(uKey.longValue(), chKey.longValue(), 0);

        // Exercise
        Comment c = commentDao.createComment(COMMENT_TEXT, uKey.longValue(), pKey.longValue());

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.comments.name()));
        assertEquals(COMMENT_TEXT, c.getComment());
    }

    @Test
    public void testFindCommentById() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        Number chKey = testInsertionUtils.createChannel();
        Number pKey = testInsertionUtils.createPost(uKey.longValue(), chKey.longValue(), 0);
        Number cKey = testInsertionUtils.createComment(uKey.longValue(), pKey.longValue());

        // Exercise
        Optional<Comment> comment = commentDao.findCommentById(cKey.longValue());

        // Validations & Post Conditions
        assertTrue(comment.isPresent());
        assertEquals(cKey.longValue(), comment.get().getCommentId());
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
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        Number chKey = testInsertionUtils.createChannel();
        Number pKey = testInsertionUtils.createPost(uKey.longValue(), chKey.longValue(), 0);
        testInsertionUtils.createComment(uKey.longValue(), pKey.longValue());

        // Exercise
        Optional<List<Comment>> comments = commentDao.findCommentsByPostId(pKey.longValue());

        // Validations & Post Conditions
        assertTrue(comments.isPresent());
        assertEquals(1, comments.get().size());
    }

    @Test
    public void testFindCommentsByInvalidPostId() {
        // Pre Conditions

        // Exercise
        Optional<List<Comment>> comments = commentDao.findCommentsByPostId(1);

        // Validations & Post Conditions
        assertFalse(comments.isPresent());
    }

/*    @Test
    public void testFindNoCommentsByPostId() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        Number chKey = testInsertionUtils.createChannel();
        Number pKey = testInsertionUtils.createPost(uKey.longValue(), chKey.longValue(), 0);

        // Exercise
        Optional<List<Comment>> comments = commentDao.findCommentsByPostId(pKey.longValue());

        // Validations & Post Conditions
        assertTrue(comments.isPresent());
        assertEquals(0, comments.get().size());
    }*/
}
