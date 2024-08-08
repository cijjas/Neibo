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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class CommentDaoImplTest {

    private static final String COMMENT_MESSAGE = "Sample Comment";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CommentDaoImpl commentDaoImpl;
    @PersistenceContext
    private EntityManager em;

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void create_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        Comment comment = commentDaoImpl.createComment(COMMENT_MESSAGE, uKey, pKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(comment);
        assertEquals(pKey, comment.getPost().getPostId().longValue());
        assertEquals(uKey, comment.getUser().getUserId().longValue());
        assertEquals(COMMENT_MESSAGE, comment.getComment());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.comments.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_commentId_postId_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> optionalComment = commentDaoImpl.findComment(cKey, pKey, nhKey);

        // Validations & Post Conditions
        assertTrue(optionalComment.isPresent());
        assertEquals(cKey, optionalComment.get().getCommentId().longValue());
    }

    @Test
    public void find_commentId_postId_neighborhoodId_invalid_commentId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> optionalComment = commentDaoImpl.findComment(INVALID_ID, pKey, nhKey);

        // Validations & Post Conditions
        assertFalse(optionalComment.isPresent());
    }


    @Test
    public void find_commentId_postId_neighborhoodId_invalid_postId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> optionalComment = commentDaoImpl.findComment(cKey, INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(optionalComment.isPresent());
    }

    @Test
    public void find_commentId_postId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> optionalComment = commentDaoImpl.findComment(cKey, pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalComment.isPresent());
    }

    @Test
    public void find_commentId_postId_neighborhoodId_invalid_commentId_postId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> optionalComment = commentDaoImpl.findComment(INVALID_ID, INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(optionalComment.isPresent());
    }

    @Test
    public void find_commentId_postId_neighborhoodId_invalid_commentId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> optionalComment = commentDaoImpl.findComment(INVALID_ID, pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalComment.isPresent());
    }

    @Test
    public void find_commentId_postId_neighborhoodId_invalid_postId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> optionalComment = commentDaoImpl.findComment(cKey, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalComment.isPresent());
    }

    @Test
    public void find_commentId_postId_neighborhoodId_invalid_commentId_postId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> optionalComment = commentDaoImpl.findComment(INVALID_ID, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalComment.isPresent());
    }

    @Test
    public void find_commentId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> optionalComment = commentDaoImpl.findComment(cKey);

        // Validations & Post Conditions
        assertTrue(optionalComment.isPresent());
        assertEquals(cKey, optionalComment.get().getCommentId().longValue());
    }

    @Test
    public void find_commentId_invalid_commentId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        Optional<Comment> optionalComment = commentDaoImpl.findComment(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalComment.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    public void get() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey, chKey, iKey);
        long pKey2 = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createComment(uKey, pKey2);
        testInserter.createComment(uKey, pKey1);
        testInserter.createComment(uKey, pKey1);

        // Exercise
        List<Comment> commentList = commentDaoImpl.getComments(pKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, commentList.size());
    }

    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        List<Comment> commentList = commentDaoImpl.getComments(pKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(commentList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    public void get_pagination() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey, chKey, iKey);
        long pKey2 = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createComment(uKey, pKey2);
        testInserter.createComment(uKey, pKey1);
        testInserter.createComment(uKey, pKey1);

        // Exercise
        List<Comment> commentList = commentDaoImpl.getComments(pKey1, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, commentList.size());
    }

    // ------------------------------------------------- COUNTS --------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createComment(uKey, pKey);

        // Exercise
        int countComments = commentDaoImpl.countComments(pKey);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countComments);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        int countComments = commentDaoImpl.countComments(pKey);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countComments);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_commentId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        boolean deleted = commentDaoImpl.deleteComment(cKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.comments.name()));
    }

    @Test
    public void delete_commentId_invalid_commentId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long cKey = testInserter.createComment(uKey, pKey);

        // Exercise
        boolean deleted = commentDaoImpl.deleteComment(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
