package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ReviewDao;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.persistence.config.TestConfig;
import com.sun.corba.se.impl.ior.WireObjectKeyTemplate;
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
    private double DELTA = 0.001;
    private String REVIEW_TEXT = "Great service!";
    private String WORKER_MAIL = "worker@test.com";
    private String REVIEWER_MAIL = "reviewer@test.com";
    private float RATING_1 = 4.0f;
    private float RATING_2 = 4.5f;
    private String REVIEW_1 = "Great service";
    private String REVIEW_2 = "Good service";

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        reviewDao = new ReviewDaoImpl(ds);
    }

    @Test
    public void testCreateReview() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInsertionUtils.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey);
        testInsertionUtils.createWorkerProfession(uKey, pKey);

        // Exercise
        Review createdReview = reviewDao.createReview(uKey, uKey2, RATING, REVIEW_TEXT);

        // Validations & Post Conditions
        assertNotNull(createdReview);
        assertEquals(uKey, createdReview.getWorkerId());
        assertEquals(uKey2, createdReview.getUserId());
        assertEquals(RATING, createdReview.getRating(), DELTA);
        assertEquals(REVIEW_TEXT, createdReview.getReview());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.reviews.name()));
    }

    @Test
    public void testGetReview() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInsertionUtils.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey);
        testInsertionUtils.createWorkerProfession(uKey, pKey);
        long rKey = testInsertionUtils.createReview(uKey, uKey2);

        // Exercise
        Review retrievedReview = reviewDao.getReview(rKey);

        // Validations & Post Conditions
        assertNotNull(retrievedReview);
    }

    @Test
    public void testGetReviews() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInsertionUtils.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey);
        testInsertionUtils.createWorkerProfession(uKey, pKey);
        testInsertionUtils.createReview(uKey, uKey2, RATING_1, REVIEW_1);
        testInsertionUtils.createReview(uKey, uKey2, RATING_2, REVIEW_2);

        // Exercise
        List<Review> reviews = reviewDao.getReviews(uKey);

        // Validations & Post Conditions
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
    }

    @Test
    public void testGetAvgRating() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInsertionUtils.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey);
        testInsertionUtils.createWorkerProfession(uKey, pKey);
        testInsertionUtils.createReview(uKey, uKey2, RATING_1, REVIEW_1);
        testInsertionUtils.createReview(uKey, uKey2, RATING_2, REVIEW_2);

        // Exercise
        float avgRating = reviewDao.getAvgRating(uKey);

        // Validations & Post Conditions
        assertEquals((RATING_1+RATING_2)/2, avgRating, DELTA); // Average of 4.0 and 4.5
    }

    @Test
    public void testGetReviewsCount() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInsertionUtils.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey);
        testInsertionUtils.createWorkerProfession(uKey, pKey);
        testInsertionUtils.createReview(uKey, uKey2, RATING_1, REVIEW_1);
        testInsertionUtils.createReview(uKey, uKey2, RATING_2, REVIEW_2);

        // Exercise
        int count = reviewDao.getReviewsCount(uKey);

        // Validations & Post Conditions
        assertEquals(2, count);
    }

    @Test
    public void testDeleteReview() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInsertionUtils.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInsertionUtils.createProfession();
        testInsertionUtils.createWorker(uKey);
        testInsertionUtils.createWorkerProfession(uKey, pKey);
        long rKey = testInsertionUtils.createReview(uKey, uKey2, RATING_1, REVIEW_1);

        // Exercise
        boolean deleted = reviewDao.deleteReview(rKey);

        // Validations & Post Conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.reviews.name()));
    }
}
