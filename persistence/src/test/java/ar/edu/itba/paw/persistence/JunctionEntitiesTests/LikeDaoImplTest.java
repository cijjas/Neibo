package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
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

import javax.sql.DataSource;

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
    private LikeDaoImpl likeDao;


    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        likeDao = new LikeDaoImpl(ds);
    }

    @Test
    public void testCreateLike() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        likeDao.createLike(pKey, uKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }

    @Test
    public void testGetLikes() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createLike(pKey, uKey);

        // Exercise
        int likes = likeDao.getLikes(pKey);

        // Validations & Post Conditions
        assertEquals(1, likes);
    }

    @Test
    public void testGetNoLikes() {
        // Pre Conditions

        // Exercise
        int likes = likeDao.getLikes(1);

        // Validations & Post Conditions
        assertEquals(0, likes);
    }

    @Test
    public void testIsPostLiked() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        testInserter.createLike(pKey, uKey);

        // Exercise
        boolean liked = likeDao.isPostLiked(pKey, uKey);

        // Validations & Post Conditions
        assertTrue(liked);
    }

    @Test
    public void testIsPostNotLiked() {
        // Pre Conditions

        // Exercise
        boolean liked = likeDao.isPostLiked(1, 1);

        // Validations & Post Conditions
        assertFalse(liked);
    }

    @Test
    public void testDeleteLike() {
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
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }

    @Test
    public void testDeleteInvalidLike() {
        // Pre Conditions

        // Exercise
        boolean deleted = likeDao.deleteLike(1, 1);

        // Validations & Post Conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }
}
