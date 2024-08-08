package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.PostDaoImpl;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class PostDaoImplTest {

    private static final String POST_TITLE_1 = "Title 1";
    private static final String POST_TITLE_2 = "Title 2";
    private static final String POST_TITLE_3 = "Title 3";
    private static final String POST_TITLE_4 = "Title 4";
    private static final String POST_TITLE_5 = "Title 5";
    private static final String POST_TITLE_6 = "Title 6";
    private static final String POST_DESCRIPTION_1 = "Desc 1";
    private static final String POST_DESCRIPTION_2 = "Desc 2";
    private static final String POST_DESCRIPTION_3 = "Desc 3";
    private static final String POST_DESCRIPTION_4 = "Desc 4";
    private static final String POST_DESCRIPTION_5 = "Desc 5";
    private static final String POST_DESCRIPTION_6 = "Desc 6";
    private static final String CHANNEL_NAME_1 = "Channel 1";
    private static final String CHANNEL_NAME_2 = "Channel 2";
    private static final String TAG_NAME_1 = "Tag 1";
    private static final String TAG_NAME_2 = "Tag 2";

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PostDaoImpl postDaoImpl;
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

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Test
    public void create_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();

        // Exercise
        Post post = postDaoImpl.createPost(POST_TITLE_1, POST_DESCRIPTION_1, uKey, chKey, iKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(post);
        assertEquals(uKey, post.getUser().getUserId().longValue());
        assertEquals(chKey, post.getChannel().getChannelId().longValue());
        assertEquals(iKey, post.getPostPicture().getImageId().longValue());
        assertEquals(POST_TITLE_1, post.getTitle());
        assertEquals(POST_DESCRIPTION_1, post.getDescription());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_postId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        Optional<Post> optionalPost = postDaoImpl.findPost(pKey);

        // Validations
        assertTrue(optionalPost.isPresent());
        assertEquals(pKey, optionalPost.get().getPostId().longValue());
    }

    @Test
    public void find_postId_invalid_postId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        Optional<Post> optionalPost = postDaoImpl.findPost(INVALID_ID);

        // Validations
        assertFalse(optionalPost.isPresent());
    }

    @Test
    public void find_postId_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        Optional<Post> optionalPost = postDaoImpl.findPost(pKey, nhKey);

        // Validations
        assertTrue(optionalPost.isPresent());
        assertEquals(pKey, optionalPost.get().getPostId().longValue());
    }

    @Test
    public void find_postId_neighborhoodId_invalid_postId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        Optional<Post> optionalPost = postDaoImpl.findPost(INVALID_ID, nhKey);

        // Validations
        assertFalse(optionalPost.isPresent());
    }

    @Test
    public void find_postId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        Optional<Post> optionalPost = postDaoImpl.findPost(pKey, INVALID_ID);

        // Validations
        assertFalse(optionalPost.isPresent());
    }

    @Test
    public void find_postId_neighborhoodId_invalid_postId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        Optional<Post> optionalPost = postDaoImpl.findPost(INVALID_ID, INVALID_ID);

        // Validations
        assertFalse(optionalPost.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        populatePosts();

        // Exercise
        List<Post> postList = postDaoImpl.getPosts(EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE, Collections.emptyList(), nhKey1, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertEquals(FOUR_ELEMENTS, postList.size());
    }

    @Test
    public void get_channelId() {
        // Pre Conditions
        populatePosts();

        // Exercise
        List<Post> postList = postDaoImpl.getPosts(chKey1, BASE_PAGE, BASE_PAGE_SIZE, Collections.emptyList(), nhKey1, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertEquals(THREE_ELEMENTS, postList.size());
    }

    @Test
    public void get_tagList() {
        // Pre Conditions
        populatePosts();
        List<Long> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(tKey2);

        // Exercise
        List<Post> postList = postDaoImpl.getPosts(EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE, TAG_LIST, nhKey2, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertEquals(TWO_ELEMENTS, postList.size());
    }

    @Test
    public void get_userId() {
        // Pre Conditions
        populatePosts();

        // Exercise
        List<Post> postList = postDaoImpl.getPosts(EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE, Collections.emptyList(), nhKey1, EMPTY_FIELD, uKey1);

        // Validations
        assertEquals(THREE_ELEMENTS, postList.size());
    }

    @Test
    public void get_channelId_tagList() {
        // Pre Conditions
        populatePosts();
        List<Long> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(tKey2);

        // Exercise
        List<Post> postList = postDaoImpl.getPosts(chKey2, BASE_PAGE, BASE_PAGE_SIZE, TAG_LIST, nhKey2, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertEquals(TWO_ELEMENTS, postList.size());
    }

    @Test
    public void get_channelId_userId() {
        // Pre Conditions
        populatePosts();

        // Exercise
        List<Post> postList = postDaoImpl.getPosts(chKey1, BASE_PAGE, BASE_PAGE_SIZE, Collections.emptyList(), nhKey1, EMPTY_FIELD, uKey1);

        // Validations
        assertEquals(TWO_ELEMENTS, postList.size());
    }

    @Test
    public void get_userId_tagList() {
        // Pre Conditions
        populatePosts();
        List<Long> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(tKey1);

        // Exercise
        List<Post> postList = postDaoImpl.getPosts(EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE, TAG_LIST, nhKey1, EMPTY_FIELD, uKey1);

        // Validations
        assertEquals(ONE_ELEMENT, postList.size());
    }

    @Test
    public void get_channelId_userId_tagList() {
        // Pre Conditions
        populatePosts();
        List<Long> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(tKey1);

        // Exercise
        List<Post> postList = postDaoImpl.getPosts(chKey1, BASE_PAGE, BASE_PAGE_SIZE, TAG_LIST, nhKey1, EMPTY_FIELD, uKey1);

        // Validations
        assertEquals(ONE_ELEMENT, postList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<Post> postList = postDaoImpl.getPosts(EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE, Collections.emptyList(), nhKey1, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertTrue(postList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        populatePosts();
        List<Long> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(tKey2);

        // Exercise
        List<Post> postList = postDaoImpl.getPosts(chKey2, TEST_PAGE, TEST_PAGE_SIZE, TAG_LIST, nhKey2, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertEquals(ONE_ELEMENT, postList.size());
    }

    // ------------------------------------------------- COUNTS --------------------------------------------------------

    // HSQL does not accept queries with SELECT COUNT(DISTINCT p.*) org.hsqldb.HsqlException: unexpected token: )
    @Test
    public void count() {
        // Pre Conditions
        populatePosts();

        // Exercise
        int countPosts = postDaoImpl.countPosts(EMPTY_FIELD, Collections.emptyList(), nhKey1, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertEquals(FOUR_ELEMENTS, countPosts);
    }

    @Test
    public void count_channelId() {
        // Pre Conditions
        populatePosts();

        // Exercise
        int countPosts = postDaoImpl.countPosts(chKey1, Collections.emptyList(), nhKey1, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertEquals(THREE_ELEMENTS, countPosts);
    }

    @Test
    public void count_tagList() {
        // Pre Conditions
        populatePosts();
        List<Long> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(tKey2);

        // Exercise
        int countPosts = postDaoImpl.countPosts(EMPTY_FIELD, TAG_LIST, nhKey2, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertEquals(TWO_ELEMENTS, countPosts);
    }

    @Test
    public void count_userId() {
        // Pre Conditions
        populatePosts();

        // Exercise
        int countPosts = postDaoImpl.countPosts(EMPTY_FIELD, Collections.emptyList(), nhKey1, EMPTY_FIELD, uKey1);

        // Validations
        assertEquals(THREE_ELEMENTS, countPosts);
    }

    @Test
    public void count_channelId_tagList() {
        // Pre Conditions
        populatePosts();
        List<Long> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(tKey2);

        // Exercise
        int countPosts = postDaoImpl.countPosts(chKey2, TAG_LIST, nhKey2, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertEquals(TWO_ELEMENTS, countPosts);
    }

    @Test
    public void count_channelId_userId() {
        // Pre Conditions
        populatePosts();

        // Exercise
        int countPosts = postDaoImpl.countPosts(chKey1, Collections.emptyList(), nhKey1, EMPTY_FIELD, uKey1);

        // Validations
        assertEquals(TWO_ELEMENTS, countPosts);
    }

    @Test
    public void count_userId_tagList() {
        // Pre Conditions
        populatePosts();
        List<Long> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(tKey1);

        // Exercise
        int countPosts = postDaoImpl.countPosts(EMPTY_FIELD, TAG_LIST, nhKey1, EMPTY_FIELD, uKey1);

        // Validations
        assertEquals(ONE_ELEMENT, countPosts);
    }

    @Test
    public void count_channelId_userId_tagList() {
        // Pre Conditions
        populatePosts();
        List<Long> TAG_LIST = new ArrayList<>();
        TAG_LIST.add(tKey1);

        // Exercise
        int countPosts = postDaoImpl.countPosts(chKey1, TAG_LIST, nhKey1, EMPTY_FIELD, uKey1);

        // Validations
        assertEquals(ONE_ELEMENT, countPosts);
    }

    @Test
    public void count_empty() {
        // Pre Conditions

        // Exercise
        int countPosts = postDaoImpl.countPosts(EMPTY_FIELD, Collections.emptyList(), nhKey1, EMPTY_FIELD, EMPTY_FIELD);

        // Validations
        assertEquals(NO_ELEMENTS, countPosts);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_postId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        boolean deleted = postDaoImpl.deletePost(pKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products.name()));
    }

    @Test
    public void delete_postId_invalid_postId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        boolean deleted = postDaoImpl.deletePost(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    // ----------------------------------------------- POPULATION ------------------------------------------------------

    private void populatePosts() {
        // [U1, C1, {}]
        // [U2, C1, {T1}]
        // [U3, C2, {T2}]
        // [U4, C2, {T1, T2}]
        // [U1, C1, {T1}]
        // [U1, C2, {}]

        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);

        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey1);
        uKey2 = testInserter.createUser(USER_MAIL_2, nhKey1);
        uKey3 = testInserter.createUser(USER_MAIL_3, nhKey2);
        uKey4 = testInserter.createUser(USER_MAIL_4, nhKey2);

        chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        chKey2 = testInserter.createChannel(CHANNEL_NAME_2);

        tKey1 = testInserter.createTag(TAG_NAME_1);
        tKey2 = testInserter.createTag(TAG_NAME_2);

        long iKey = testInserter.createImage();
        long pKey1 = testInserter.createPost(POST_TITLE_1, POST_DESCRIPTION_1, uKey1, chKey1, iKey);
        long pKey2 = testInserter.createPost(POST_TITLE_2, POST_DESCRIPTION_2, uKey2, chKey1, iKey);
        testInserter.createCategorization(tKey1, pKey2);
        long pKey3 = testInserter.createPost(POST_TITLE_3, POST_DESCRIPTION_3, uKey3, chKey2, iKey);
        testInserter.createCategorization(tKey2, pKey3);
        long pKey4 = testInserter.createPost(POST_TITLE_4, POST_DESCRIPTION_4, uKey4, chKey2, iKey);
        testInserter.createCategorization(tKey1, pKey4);
        testInserter.createCategorization(tKey2, pKey4);
        long pKey5 = testInserter.createPost(POST_TITLE_5, POST_DESCRIPTION_5, uKey1, chKey1, iKey);
        testInserter.createCategorization(tKey1, pKey5);
        testInserter.createPost(POST_TITLE_6, POST_DESCRIPTION_6, uKey1, chKey2, iKey);
    }
}
