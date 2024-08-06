package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.persistence.JunctionDaos.ReviewDaoImpl;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ReviewDaoImplTest {

    private final float RATING = 4.5f;
    private final double DELTA = 0.001;
    private final String REVIEW_TEXT = "Great service!";
    private final String WORKER_MAIL = "worker@test.com";
    private final String REVIEWER_MAIL = "reviewer@test.com";
    private final float RATING_1 = 4.0f;
    private final float RATING_2 = 4.5f;
    private final String REVIEW_1 = "Great service";
    private final String REVIEW_2 = "Good service";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ReviewDaoImpl reviewDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateReview() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInserter.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        Review createdReview = reviewDao.createReview(uKey, uKey2, RATING, REVIEW_TEXT);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(createdReview);
        assertEquals(uKey, createdReview.getWorker().getUser().getUserId().longValue());
        assertEquals(uKey2, createdReview.getUser().getUserId().longValue());
        assertEquals(RATING, createdReview.getRating(), DELTA);
        assertEquals(REVIEW_TEXT, createdReview.getReview());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.reviews.name()));
    }

    @Test
    public void testGetReviews() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInserter.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey = testInserter.createReview(uKey, uKey2);

        // Exercise
        Optional<Review> retrievedReview = reviewDao.findReview(rKey);

        // Validations & Post Conditions
        assertTrue(retrievedReview.isPresent());
    }

    @Test
    public void testGetNoReviews() {
        // Pre Conditions

        // Exercise
        List<Review> reviews = reviewDao.getReviews(1);

        // Validations & Post Conditions
        assertTrue(reviews.isEmpty());
    }

    @Test
    public void testGetAvgRating() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInserter.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        testInserter.createReview(uKey, uKey2, RATING_1, REVIEW_1);
        testInserter.createReview(uKey, uKey2, RATING_2, REVIEW_2);

        // Exercise
        Float avgRating = reviewDao.getAvgRating(uKey);

        // Validations & Post Conditions
//        assertTrue(maybeAvgRating.isPresent());
        assertEquals((RATING_1 + RATING_2) / 2, avgRating, DELTA); // Average of 4.0 and 4.5
    }

    @Test
    public void testGetNullAvgRating() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInserter.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        Float avgRating = reviewDao.getAvgRating(uKey);

        // Validations & Post Conditions
//        assertTrue(maybeAvgRating.isPresent());
        assertEquals(0.0f, avgRating, DELTA);
    }

    @Test
    public void testGetReviewsCount() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInserter.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        testInserter.createReview(uKey, uKey2, RATING_1, REVIEW_1);
        testInserter.createReview(uKey, uKey2, RATING_2, REVIEW_2);

        // Exercise
        int count = reviewDao.countReviews(uKey);

        // Validations & Post Conditions
        assertEquals(2, count);
    }

    @Test
    public void testGetReviewsCountZero() {
        // Pre Conditions

        // Exercise
        int count = reviewDao.countReviews(1);

        // Validations & Post Conditions
        assertEquals(0, count);
    }

    @Test
    public void testDeleteReview() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(WORKER_MAIL, nhKey);
        long uKey2 = testInserter.createUser(REVIEWER_MAIL, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey = testInserter.createReview(uKey, uKey2, RATING_1, REVIEW_1);

        // Exercise
        boolean deleted = reviewDao.deleteReview(rKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.reviews.name()));
    }

    @Test
    public void testDeleteInvalidReview() {
        // Pre Conditions

        // Exercise
        boolean deleted = reviewDao.deleteReview(1);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
