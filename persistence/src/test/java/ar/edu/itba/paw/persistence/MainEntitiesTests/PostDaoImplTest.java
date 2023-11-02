package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.MainEntities.Post;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class PostDaoImplTest {

    private static final String TITLE_1 = "Title 1";
    private static final String TITLE_2 = "Title 2";
    private static final String TITLE_3 = "Title 3";
    private static final String TITLE_4 = "Title 4";
    private static final String DESC_1 = "Desc 1";
    private static final String DESC_2 = "Desc 2";
    private static final String DESC_3 = "Desc 3";
    private static final String DESC_4 = "Desc 4";
    private static final String NH_NAME_1 = "Neighborhood 1";
    private static final String NH_NAME_2 = "Neighborhood 2";
    private static final String USER_MAIL_1 = "poster1-1@test.com";
    private static final String USER_MAIL_2 = "poster2-1@test.com";
    private static final String USER_MAIL_3 = "poster3-2@test.com";
    private static final String USER_MAIL_4 = "poster4-2@test.com";
    private static final String USER_MAIL_5 = "liker5-2@test.com";
    private static final String USER_MAIL_6 = "liker6-2@test.com";
    private static final String USER_MAIL_7 = "liker7-2@test.com";
    private static final String USER_MAIL_8 = "liker8-2@test.com";
    private static final String USER_MAIL_9 = "liker9-2@test.com";
    private static final String CHANNEL_NAME_1 = "Channel 1";
    private static final String CHANNEL_NAME_2 = "Channel 2";
    private static final String TAG_NAME_1 = "Tag1";
    private static final String TAG_NAME_2 = "Tag2";
    private static final int BASE_PAGE = 1;
    private static final int BASE_PAGE_SIZE = 10;
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PostDao postDao;
    private long nhKey1;
    private long nhKey2;
    private long tKey1;
    private long tKey2;
    private long uKey1;
    private long uKey2;
    private long uKey3;
    private long uKey4;
    private long chKey1;
    private long chKey2;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreatePost() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();

        // Exercise
        Post createdPost = postDao.createPost(TITLE_1, DESC_1, uKey, chKey, iKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(createdPost);
        assertEquals(TITLE_1, createdPost.getTitle());
        assertEquals(DESC_1, createdPost.getDescription());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts.name()));
    }

    @Test
    public void testFindPostById() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        Optional<Post> maybePost = postDao.findPostById(pKey);

        // Validations
        assertTrue(maybePost.isPresent());
    }

    @Test
    public void testFindPostByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Post> maybePost = postDao.findPostById(1);

        // Validations
        assertFalse(maybePost.isPresent());
    }

    @Test
    public void testGetPostsByCriteriaNeighborhood() {
        // Pre Conditions
        populatePosts();

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(null, BASE_PAGE, BASE_PAGE_SIZE, null, nhKey1, PostStatus.none, 0);

        // Validations
        assertEquals(2, retrievedPosts.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetPostsByCriteriaChannelAndNeighborhood() {
        // Pre Conditions
        populatePosts();

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(CHANNEL_NAME_1, BASE_PAGE, BASE_PAGE_SIZE, null, nhKey1, PostStatus.none, 0);


        // Validations
        assertEquals(2, retrievedPosts.size());
    }

    @Test
    public void testGetPostsByCriteriaChannelAndNeighborhoodAndTag() {
        // Pre Conditions
        populatePosts();
        List<String> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(TAG_NAME_1);

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(CHANNEL_NAME_1, BASE_PAGE, BASE_PAGE_SIZE, TAG_LIST, nhKey1, PostStatus.none, 0);

        // Validations
        assertEquals(1, retrievedPosts.size());
    }

    @Test
    public void testGetPostsByCriteriaChannelAndNeighborhoodAndMultipleTag() {
        // Pre Conditions
        populatePosts();
        List<String> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(TAG_NAME_2);

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(CHANNEL_NAME_2, BASE_PAGE, BASE_PAGE_SIZE, TAG_LIST, nhKey2, PostStatus.none, 0);

        // Validations
        assertEquals(2, retrievedPosts.size());
    }

    @Test
    public void testGetPostsByCriteriaChannelAndNeighborhoodAndMultipleTagAndSize() {
        // Pre Conditions
        populatePosts();
        List<String> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(TAG_NAME_2);

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(CHANNEL_NAME_2, BASE_PAGE, 1, TAG_LIST, nhKey2, PostStatus.none, 0);

        // Validations
        assertEquals(1, retrievedPosts.size());
    }

    @Test
    public void testGetPostsByCriteriaChannelAndNeighborhoodAndMultipleTagAndSizeAndPage() {
        // Pre Conditions
        populatePosts();
        List<String> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(TAG_NAME_2);

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(CHANNEL_NAME_2, 2, 1, TAG_LIST, nhKey2, PostStatus.none, 0);

        // Validations
        assertEquals(1, retrievedPosts.size());
    }

    // ------------------ !!! HOT & TRENDING POSTS CANT BE TESTED AS HSQL DOES NOT ACCEPT INTERVAL !!! -----------------

    private void populatePosts() {
        /*
         * 4 Posts
         * Post 1 -> In Neighborhood 1, By User 1, with no tags
         * Post 2 -> In Neighborhood 1, By User 2, with tag 1
         * Post 3 -> In Neighborhood 2, By User 3, with tag 2
         * Post 4 -> In Neighborhood 2, By User 4, with tag 1 & tag 2
         */

        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NH_NAME_1);
        nhKey2 = testInserter.createNeighborhood(NH_NAME_2);

        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);
        uKey2 = testInserter.createUser(USER_MAIL_2, nhKey1);
        uKey3 = testInserter.createUser(USER_MAIL_3, nhKey2);
        uKey4 = testInserter.createUser(USER_MAIL_4, nhKey2);

        chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        chKey2 = testInserter.createChannel(CHANNEL_NAME_2);

        tKey1 = testInserter.createTag(TAG_NAME_1);
        tKey2 = testInserter.createTag(TAG_NAME_2);

        long pKey1 = testInserter.createPost(TITLE_1, DESC_1, uKey1, chKey1, 0);
        long pKey2 = testInserter.createPost(TITLE_2, DESC_2, uKey2, chKey1, 0);
        testInserter.createCategorization(tKey1, pKey2);
        long pKey3 = testInserter.createPost(TITLE_3, DESC_3, uKey3, chKey2, 0);
        testInserter.createCategorization(tKey2, pKey3);
        long pKey4 = testInserter.createPost(TITLE_4, DESC_4, uKey4, chKey2, 0);
        testInserter.createCategorization(tKey1, pKey4);
        testInserter.createCategorization(tKey2, pKey4);

        // Comments to become hot
        testInserter.createComment(uKey4, pKey4);
        testInserter.createComment(uKey4, pKey4);
        testInserter.createComment(uKey4, pKey4);
        testInserter.createComment(uKey4, pKey4);
        testInserter.createComment(uKey4, pKey4);
    }
}
