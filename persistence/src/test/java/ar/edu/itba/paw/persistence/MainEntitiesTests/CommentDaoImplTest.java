package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.CommentDaoImpl;
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
public class CommentDaoImplTest {

    private static final String COMMENT_TEXT = "Sample Comment";
    private static final int BASE_PAGE = 1;
    private static final int BASE_PAGE_SIZE = 10;
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CommentDaoImpl commentDao;
    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateComment() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        Comment c = commentDao.createComment(COMMENT_TEXT, uKey, pKey);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.comments.name()));
        assertEquals(COMMENT_TEXT, c.getComment());
    }

    @Test
    public void testFindCommentById() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> comment = commentDao.findComment(cKey);

        // Validations & Post Conditions
        assertTrue(comment.isPresent());
        assertEquals(cKey, comment.get().getCommentId().longValue());
    }

    @Test
    public void testFindCommentByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Comment> comment = commentDao.findComment(1);

        // Validations & Post Conditions
        assertFalse(comment.isPresent());
    }

    @Test
    public void testFindCommentsByPostId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createComment(uKey, pKey);

        // Exercise
        List<Comment> comments = commentDao.getComments(pKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
    }

    @Test
    public void testFindCommentsByInvalidPostId() {
        // Pre Conditions

        // Exercise
        List<Comment> comments = commentDao.getComments(1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(comments.isEmpty());
    }

    public void testGetComments(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey, chKey, iKey);
        long pKey2 = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createComment(uKey, pKey2);
        // Same user commenting the same thing twice
        testInserter.createComment(uKey, pKey1);
        testInserter.createComment(uKey, pKey1);

        // Exercise
        List<Comment> comments = commentDao.getComments(pKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertFalse(comments.isEmpty());
        assertEquals(2, comments.size());
    }

    public void testGetNoComments(){
        // Pre Conditions


        // Exercise
        List<Comment> comments = commentDao.getComments(1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(comments.isEmpty());
    }

    @Test
    public void testGetCommentsCountByPostId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createComment(uKey, pKey);

        // Exercise
        int comments = commentDao.countComments(pKey);

        // Validations & Post Conditions
        assertEquals(1, comments);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.comments.name()));
    }

    @Test
    public void testGetCommentsCountByPostInvalidId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        int comments = commentDao.countComments(pKey);

        // Validations & Post Conditions
        assertEquals(0, comments);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.comments.name()));
    }
}
