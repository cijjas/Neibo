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

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LikeDaoImpl likeDao;
    @PersistenceContext
    private EntityManager em;

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
        likeDao.createLike(pKey, uKey);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }
    @Test
    public void get_neighborhoodId(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey1, chKey, iKey);
        long pKey2 = testInserter.createPost(uKey2, chKey, iKey);
        testInserter.createLike(pKey1, uKey1);
        testInserter.createLike(pKey2, uKey1);
        testInserter.createLike(pKey1, uKey2);

        // Exercise
        List<Like> attendances = likeDao.getLikes(null, null, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(3, attendances.size());
    }

    @Test
    public void get_neighborhoodId_userId(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey1, chKey, iKey);
        long pKey2 = testInserter.createPost(uKey2, chKey, iKey);
        testInserter.createLike(pKey1, uKey1);
        testInserter.createLike(pKey2, uKey1);
        testInserter.createLike(pKey1, uKey2);

        // Exercise
        List<Like> attendances = likeDao.getLikes(null, uKey1, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(2, attendances.size());
    }

    @Test
    public void get_neighborhoodId_postId(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey1, chKey, iKey);
        long pKey2 = testInserter.createPost(uKey2, chKey, iKey);
        testInserter.createLike(pKey1, uKey1);
        testInserter.createLike(pKey2, uKey1);
        testInserter.createLike(pKey1, uKey2);

        // Exercise
        List<Like> attendances = likeDao.getLikes(pKey1, null, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(2, attendances.size());
    }

    @Test
    public void get_neighborhoodId_userId_postId(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey1, chKey, iKey);
        long pKey2 = testInserter.createPost(uKey2, chKey, iKey);
        testInserter.createLike(pKey1, uKey1);
        testInserter.createLike(pKey2, uKey1);
        testInserter.createLike(pKey1, uKey2);

        // Exercise
        List<Like> attendances = likeDao.getLikes(pKey1, uKey1, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(1, attendances.size());
    }

    @Test
    public void get_empty(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(uKey1, chKey, iKey);
        long pKey2 = testInserter.createPost(uKey2, chKey, iKey);

        // Exercise
        List<Like> attendances = likeDao.getLikes(pKey1, uKey1, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(0, attendances.size());
    }

    @Test
    public void count_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createLike(pKey, uKey);

        // Exercise
        int likes = likeDao.countLikes(pKey, uKey, nhKey);

        // Validations & Post Conditions
        assertEquals(1, likes);
    }

    @Test
    public void count_empty() {
        // Pre Conditions

        // Exercise
        int likes = likeDao.countLikes(INVALID_LONG_ID, INVALID_LONG_ID, INVALID_ID);

        // Validations & Post Conditions
        assertEquals(0, likes);
    }

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
        boolean deleted = likeDao.deleteLike(pKey, uKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
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
        boolean deleted = likeDao.deleteLike(INVALID_ID, uKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
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
        boolean deleted = likeDao.deleteLike(pKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
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
        boolean deleted = likeDao.deleteLike(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }
}
