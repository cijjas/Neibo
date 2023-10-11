package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.persistence.config.TestConfig;
import enums.SortOrder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes =TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class PostDaoImplTest {

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;


    private ChannelDao channelDao;
    private UserDao userDao;
    private TagDao tagDao;
    private LikeDao likeDao;
    private PostDao postDao;
    private BookingDao bookingDao;
    private ShiftDao shiftDao;
    private AmenityDao amenityDao;
    private DayDao dayDao;
    private TimeDao timeDao;

    // Variables for test data
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
    private static final boolean NOT_HOT = false;
    private static final boolean HOT = true;
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

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        channelDao = new ChannelDaoImpl(ds);
        dayDao = new DayDaoImpl(ds);
        timeDao = new TimeDaoImpl(ds);
        shiftDao = new ShiftDaoImpl(ds, dayDao, timeDao);
        amenityDao = new AmenityDaoImpl(ds, shiftDao);
        bookingDao = new BookingDaoImpl(ds, shiftDao, amenityDao);
        userDao = new UserDaoImpl(ds, bookingDao);
        tagDao = new TagDaoImpl(ds);
        likeDao = new LikeDaoImpl(ds);
        postDao = new PostDaoImpl(ds, channelDao, userDao, tagDao, likeDao);
    }

    @Test
    public void testCreatePost() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long chKey = testInsertionUtils.createChannel();
        long iKey = testInsertionUtils.createImage();

        // Exercise
        Post createdPost = postDao.createPost(TITLE_1, DESC_1, uKey, chKey, iKey);

        // Validations & Post Conditions
        assertNotNull(createdPost);
        assertEquals(TITLE_1, createdPost.getTitle());
        assertEquals(DESC_1, createdPost.getDescription());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts.name()));
    }

    @Test
    public void testFindPostById() {
        // Pre Conditions
        // Insert a test post into the database
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long chKey = testInsertionUtils.createChannel();
        long iKey = testInsertionUtils.createImage();
        long pKey = testInsertionUtils.createPost(uKey, chKey, iKey);

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

    // Retrieval of all posts in a certain neighborhood
    @Test
    public void testGetPostsByCriteriaNeighborhood() {
        // Pre Conditions
        populatePosts();

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(null, BASE_PAGE, BASE_PAGE_SIZE, null, nhKey1, NOT_HOT, 0);

        // Validations
        assertEquals(2, retrievedPosts.size()); // Adjust based on the expected number of retrieved posts
    }

    // Retrieval of all posts in a certain neighborhood's channel
    @Test
    public void testGetPostsByCriteriaChannelAndNeighborhood() {
        // Pre Conditions
        populatePosts();

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(CHANNEL_NAME_1, BASE_PAGE, BASE_PAGE_SIZE, null, nhKey1, NOT_HOT, 0);


        // Validations
        assertEquals(2, retrievedPosts.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetPostsByCriteriaChannelAndNeighborhoodAndTag() {
        // Pre Conditions
        populatePosts();
        List<String> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(TAG_NAME_1);

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(CHANNEL_NAME_1, BASE_PAGE, BASE_PAGE_SIZE, TAG_LIST, nhKey1, NOT_HOT, 0);

        // Validations
        assertEquals(1, retrievedPosts.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetPostsByCriteriaChannelAndNeighborhoodAndMultipleTag() {
        // Pre Conditions
        populatePosts();
        List<String> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(TAG_NAME_2);

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(CHANNEL_NAME_2, BASE_PAGE, BASE_PAGE_SIZE, TAG_LIST, nhKey2, NOT_HOT, 0);

        // Validations
        assertEquals(2, retrievedPosts.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetPostsByCriteriaChannelAndNeighborhoodAndMultipleTagAndSize() {
        // Pre Conditions
        populatePosts();
        List<String> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(TAG_NAME_2);

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(CHANNEL_NAME_2, BASE_PAGE, 1, TAG_LIST, nhKey2, NOT_HOT, 0);

        // Validations
        assertEquals(1, retrievedPosts.size()); // Adjust based on the expected number of retrieved posts
    }

    @Test
    public void testGetPostsByCriteriaChannelAndNeighborhoodAndMultipleTagAndSizeAndPage() {
        // Pre Conditions
        populatePosts();
        List<String> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(TAG_NAME_2);

        // Exercise
        List<Post> retrievedPosts = postDao.getPostsByCriteria(CHANNEL_NAME_2, 2, 1, TAG_LIST, nhKey2, NOT_HOT, 0);

        // Validations
        assertEquals(1, retrievedPosts.size()); // Adjust based on the expected number of retrieved posts
    }


    // ------------------ !!! HOT & TRENDING POSTS CANT BE TESTED AS HSQL DOES NOT ACCEPT INTERVAL !!! -----------------
    // ------ !!! SAME ISSUE WITH THE COUNT FUNCTION, BUT IT IS JUST AN EXTENSION OF THE GETPOSTSBYCRITERIA() !!! ------


    private void populatePosts() {
        /*
         * 4 Posts
         * Post 1 -> In Neighborhood 1, By User 1, with no tags
         * Post 2 -> In Neighborhood 1, By User 2, with tag 1
         * Post 3 -> In Neighborhood 2, By User 3, with tag 2
         * Post 4 -> In Neighborhood 2, By User 4, with tag 1 & tag 2
         */

        // Pre Conditions
        nhKey1 = testInsertionUtils.createNeighborhood(NH_NAME_1);
        nhKey2 = testInsertionUtils.createNeighborhood(NH_NAME_2);

        uKey1 = testInsertionUtils.createUser(USER_MAIL_1, nhKey1);
        uKey2 = testInsertionUtils.createUser(USER_MAIL_2, nhKey1);
        uKey3 = testInsertionUtils.createUser(USER_MAIL_3, nhKey2);
        uKey4 = testInsertionUtils.createUser(USER_MAIL_4, nhKey2);

        chKey1 = testInsertionUtils.createChannel(CHANNEL_NAME_1);
        chKey2 = testInsertionUtils.createChannel(CHANNEL_NAME_2);

        tKey1 = testInsertionUtils.createTag(TAG_NAME_1);
        tKey2 = testInsertionUtils.createTag(TAG_NAME_2);

        long pKey1 = testInsertionUtils.createPost(TITLE_1, DESC_1, uKey1, chKey1, 0);
        long pKey2 = testInsertionUtils.createPost(TITLE_2, DESC_2, uKey2, chKey1, 0);
        testInsertionUtils.createCategorization(tKey1, pKey2);
        long pKey3 = testInsertionUtils.createPost(TITLE_3, DESC_3, uKey3, chKey2, 0);
        testInsertionUtils.createCategorization(tKey2, pKey3);
        long pKey4 = testInsertionUtils.createPost(TITLE_4, DESC_4, uKey4, chKey2, 0);
        testInsertionUtils.createCategorization(tKey1, pKey4);
        testInsertionUtils.createCategorization(tKey2, pKey4);

        // Comments to become hot
        testInsertionUtils.createComment(uKey4, pKey4);
        testInsertionUtils.createComment(uKey4, pKey4);
        testInsertionUtils.createComment(uKey4, pKey4);
        testInsertionUtils.createComment(uKey4, pKey4);
        testInsertionUtils.createComment(uKey4, pKey4);
    }
}
