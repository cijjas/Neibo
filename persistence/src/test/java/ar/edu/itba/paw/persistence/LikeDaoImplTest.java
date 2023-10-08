package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.persistence.LikeDaoImpl;
import ar.edu.itba.paw.persistence.Table;
import ar.edu.itba.paw.persistence.TestInsertionUtils;
import ar.edu.itba.paw.persistence.config.TestConfig;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class LikeDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private LikeDaoImpl likeDao;

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        likeDao = new LikeDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateLike() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        Number chKey = testInsertionUtils.createChannel();
        Number pKey = testInsertionUtils.createPost(uKey.longValue(), chKey.longValue(), 0);

        // Exercise
        likeDao.createLike(pKey.longValue(), uKey.longValue());

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }

    @Test
    public void testGetLikes() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        Number chKey = testInsertionUtils.createChannel();
        Number pKey = testInsertionUtils.createPost(uKey.longValue(), chKey.longValue(), 0);
        testInsertionUtils.createLike(pKey.longValue(), uKey.longValue());

        // Exercise
        int likes = likeDao.getLikes(pKey.longValue());

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
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        Number chKey = testInsertionUtils.createChannel();
        Number pKey = testInsertionUtils.createPost(uKey.longValue(), chKey.longValue(), 0);
        testInsertionUtils.createLike(pKey.longValue(), uKey.longValue());

        // Exercise
        boolean liked = likeDao.isPostLiked(pKey.longValue(), uKey.longValue());

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
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        Number chKey = testInsertionUtils.createChannel();
        Number pKey = testInsertionUtils.createPost(uKey.longValue(), chKey.longValue(), 0);
        testInsertionUtils.createLike(pKey.longValue(), uKey.longValue());

        // Exercise
        boolean deleted = likeDao.deleteLike(pKey.longValue(), uKey.longValue());

        // Validations & Post Conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }

    public void testDeleteInvalidLike() {
        // Pre Conditions

        // Exercise
        boolean deleted = likeDao.deleteLike(1, 1);

        // Validations & Post Conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }
}
