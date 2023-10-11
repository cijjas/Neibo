package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ReviewDao;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class ReviewDaoImplTest {

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private ReviewDao reviewDao;

    private float RATING = 4.5f;
    private String REVIEW_TEXT = "Great service!";

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        reviewDao = new ReviewDaoImpl(ds);
    }

    @Test
    public void testCreateReview() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser("worker@test.com", nhKey.longValue());
        Number uKey2 = testInsertionUtils.createUser("reviewer@test.com", nhKey.longValue());
        Number pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey.longValue());
        testInsertionUtils.createWorkerProfession(uKey.longValue(), pKey.longValue());

        // Exercise

        Review createdReview = reviewDao.createReview(uKey.longValue(), uKey2.longValue(), RATING, REVIEW_TEXT);

        // Validations & Post Conditions
        assertNotNull(createdReview);
        assertEquals(uKey.longValue(), createdReview.getWorkerId());
        assertEquals(uKey2.longValue(), createdReview.getUserId());
        assertEquals(RATING, createdReview.getRating(), 0.001);
        assertEquals(REVIEW_TEXT, createdReview.getReview());
    }

    @Test
    public void testGetReview() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser("worker@test.com", nhKey.longValue());
        Number uKey2 = testInsertionUtils.createUser("reviewer@test.com", nhKey.longValue());
        Number pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey.longValue());
        testInsertionUtils.createWorkerProfession(uKey.longValue(), pKey.longValue());
        Number rKey = testInsertionUtils.createReview(uKey.longValue(), uKey2.longValue());

        // Exercise
        Review retrievedReview = reviewDao.getReview(rKey.longValue());

        // Validations & Post Conditions
        assertNotNull(retrievedReview);
    }

    @Test
    public void testGetReviews() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser("worker@test.com", nhKey.longValue());
        Number uKey2 = testInsertionUtils.createUser("reviewer@test.com", nhKey.longValue());
        Number pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey.longValue());
        testInsertionUtils.createWorkerProfession(uKey.longValue(), pKey.longValue());
        testInsertionUtils.createReview(uKey.longValue(), uKey2.longValue(), 4.0f, "Good service");
        testInsertionUtils.createReview(uKey.longValue(), uKey2.longValue(), 4.5f, "Great service");

        // Exercise
        List<Review> reviews = reviewDao.getReviews(uKey.longValue());

        // Validations & Post Conditions
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
    }

    @Test
    public void testGetAvgRating() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser("worker@test.com", nhKey.longValue());
        Number uKey2 = testInsertionUtils.createUser("reviewer@test.com", nhKey.longValue());
        Number pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey.longValue());
        testInsertionUtils.createWorkerProfession(uKey.longValue(), pKey.longValue());
        testInsertionUtils.createReview(uKey.longValue(), uKey2.longValue(), 4.0f, "Good service");
        testInsertionUtils.createReview(uKey.longValue(), uKey2.longValue(), 4.5f, "Great service");

        // Exercise
        float avgRating = reviewDao.getAvgRating(uKey.longValue());

        // Validations & Post Conditions
        assertEquals(4.25f, avgRating, 0.001); // Average of 4.0 and 4.5
    }

    @Test
    public void testGetReviewsCount() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser("worker@test.com", nhKey.longValue());
        Number uKey2 = testInsertionUtils.createUser("reviewer@test.com", nhKey.longValue());
        Number pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey.longValue());
        testInsertionUtils.createWorkerProfession(uKey.longValue(), pKey.longValue());
        testInsertionUtils.createReview(uKey.longValue(), uKey2.longValue(), 4.0f, "Good service");
        testInsertionUtils.createReview(uKey.longValue(), uKey2.longValue(), 4.5f, "Great service");

        // Exercise
        int count = reviewDao.getReviewsCount(uKey.longValue());

        // Validations & Post Conditions
        assertEquals(2, count);
    }

    @Test
    public void testDeleteReview() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser("worker@test.com", nhKey.longValue());
        Number uKey2 = testInsertionUtils.createUser("reviewer@test.com", nhKey.longValue());
        Number pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey.longValue());
        testInsertionUtils.createWorkerProfession(uKey.longValue(), pKey.longValue());
        Number rKey = testInsertionUtils.createReview(uKey.longValue(), uKey2.longValue(), 4.0f, "Good service");

        // Exercise
        boolean deleted = reviewDao.deleteReview(rKey.longValue());

        // Validations & Post Conditions
        assertTrue(deleted);
    }
}
