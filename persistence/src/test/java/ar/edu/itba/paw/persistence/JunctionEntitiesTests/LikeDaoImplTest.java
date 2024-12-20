package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.persistence.JunctionDaos.LikeDaoImpl;
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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class LikeDaoImplTest {

    private static long nhKey;
    private static long uKey1;
    private static long uKey2;
    private static long pKey1;
    private static long pKey2;
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LikeDaoImpl likeDaoImpl;
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
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        Like like = likeDaoImpl.createLike(uKey, pKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(like);
        assertEquals(pKey, like.getPost().getPostId().longValue());
        assertEquals(uKey, like.getUser().getUserId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        populateLikes();

        // Exercise
        List<Like> likeList = likeDaoImpl.getLikes(EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, likeList.size());
    }

    @Test
    public void get_userId() {
        // Pre Conditions
        populateLikes();

        // Exercise
        List<Like> likeList = likeDaoImpl.getLikes(uKey1, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, likeList.size());
    }

    @Test
    public void get_postId() {
        // Pre Conditions
        populateLikes();

        // Exercise
        List<Like> likeList = likeDaoImpl.getLikes(EMPTY_FIELD, pKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, likeList.size());
    }

    @Test
    public void get_userId_postId() {
        // Pre Conditions
        populateLikes();

        // Exercise
        List<Like> likeList = likeDaoImpl.getLikes(uKey1, pKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, likeList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey1, chKey, iKey);
        long pKey2 = testInserter.createPost(uKey2, chKey, iKey);

        // Exercise
        List<Like> likeList = likeDaoImpl.getLikes(uKey1, pKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(likeList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        populateLikes();

        // Exercise
        List<Like> likeList = likeDaoImpl.getLikes(EMPTY_FIELD, EMPTY_FIELD, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, likeList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        populateLikes();

        // Exercise
        int countLikes = likeDaoImpl.countLikes(EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countLikes);
    }

    @Test
    public void count_userId() {
        // Pre Conditions
        populateLikes();

        // Exercise
        int countLikes = likeDaoImpl.countLikes(uKey1, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countLikes);
    }

    @Test
    public void count_postId() {
        // Pre Conditions
        populateLikes();

        // Exercise
        int countLikes = likeDaoImpl.countLikes(EMPTY_FIELD, pKey1);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countLikes);
    }

    @Test
    public void count_userId_postId() {
        // Pre Conditions
        populateLikes();

        // Exercise
        int countLikes = likeDaoImpl.countLikes(uKey1, pKey1);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countLikes);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey1, chKey, iKey);
        long pKey2 = testInserter.createPost(uKey2, chKey, iKey);

        // Exercise
        int countLikes = likeDaoImpl.countLikes(uKey1, pKey1);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countLikes);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_postId_userId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createLike(pKey, uKey);

        // Exercise
        boolean deleted = likeDaoImpl.deleteLike(uKey, pKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }

    @Test
    public void delete_postId_userId_invalid_postId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createLike(pKey, uKey);

        // Exercise
        boolean deleted = likeDaoImpl.deleteLike(uKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_postId_userId_invalid_userId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createLike(pKey, uKey);

        // Exercise
        boolean deleted = likeDaoImpl.deleteLike(INVALID_ID, pKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_postId_userId_invalid_postId_userId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createLike(pKey, uKey);

        // Exercise
        boolean deleted = likeDaoImpl.deleteLike(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    // ----------------------------------------------- POPULATION ------------------------------------------------------

    private void populateLikes() {
        nhKey = testInserter.createNeighborhood();
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        pKey1 = testInserter.createPost(uKey1, chKey, iKey);
        pKey2 = testInserter.createPost(uKey2, chKey, iKey);
        testInserter.createLike(pKey1, uKey1);
        testInserter.createLike(pKey2, uKey1);
        testInserter.createLike(pKey1, uKey2);
    }
}
