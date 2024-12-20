package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.persistence.JunctionDaos.ReviewDaoImpl;
import ar.edu.itba.paw.persistence.TestConstants;
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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ReviewDaoImplTest {

    private final double DELTA = 0.001;
    private final float REVIEW_RATING_1 = 4.0f;
    private final float REVIEW_RATING_2 = 4.5f;
    private final String REVIEW_MESSAGE_1 = "Great service";
    private final String REVIEW_MESSAGE_2 = "Good service";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ReviewDaoImpl ReviewDaoImpl;

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
        long uKey = testInserter.createUser(TestConstants.USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(TestConstants.USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        Review review = ReviewDaoImpl.createReview(uKey, uKey2, REVIEW_RATING_1, REVIEW_MESSAGE_1);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(review);
        assertEquals(uKey, review.getWorker().getUser().getUserId().longValue());
        assertEquals(uKey2, review.getUser().getUserId().longValue());
        assertEquals(REVIEW_RATING_1, review.getRating(), DELTA);
        assertEquals(REVIEW_MESSAGE_1, review.getReview());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.reviews.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_reviewId_workerId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey = testInserter.createReview(uKey, uKey2);

        // Exercise
        Optional<Review> optionalReview = ReviewDaoImpl.findReview(uKey, rKey);

        // Validations & Post Conditions
        assertTrue(optionalReview.isPresent());
        assertEquals(rKey, optionalReview.get().getReviewId().longValue());
    }

    @Test
    public void find_reviewId_workerId_invalid_reviewId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey = testInserter.createReview(uKey, uKey2);

        // Exercise
        Optional<Review> optionalReview = ReviewDaoImpl.findReview(uKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalReview.isPresent());
    }

    @Test
    public void find_reviewId_workerId_invalid_workerId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey = testInserter.createReview(uKey, uKey2);

        // Exercise
        Optional<Review> optionalReview = ReviewDaoImpl.findReview(INVALID_ID, rKey);

        // Validations & Post Conditions
        assertFalse(optionalReview.isPresent());
    }

    @Test
    public void find_reviewId_workerId_invalid_reviewId_workerId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey = testInserter.createReview(uKey, uKey2);

        // Exercise
        Optional<Review> optionalReview = ReviewDaoImpl.findReview(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalReview.isPresent());
    }

    @Test
    public void findLatestReview_workerId_userId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey1 = testInserter.createReview(uKey, uKey2, REVIEW_RATING_1, REVIEW_MESSAGE_1, DATE_1);
        long rKey2 = testInserter.createReview(uKey, uKey2, REVIEW_RATING_2, REVIEW_MESSAGE_2, DATE_2);

        // Exercise
        Optional<Review> optionalReview = ReviewDaoImpl.findLatestReview(uKey, uKey2);


        // Validations & Post Conditions
        assertTrue(optionalReview.isPresent());
        assertEquals(rKey2, optionalReview.get().getReviewId().longValue());
    }

    @Test
    public void findLatestReview_workerId_userId_invalid_workerId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey1 = testInserter.createReview(uKey, uKey2);
        long rKey2 = testInserter.createReview(uKey, uKey2);

        // Exercise
        Optional<Review> optionalReview = ReviewDaoImpl.findLatestReview(INVALID_ID, uKey2);

        // Validations & Post Conditions
        assertFalse(optionalReview.isPresent());
    }

    @Test
    public void findLatestReview_workerId_userId_invalid_userId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey1 = testInserter.createReview(uKey, uKey2);
        long rKey2 = testInserter.createReview(uKey, uKey2);

        // Exercise
        Optional<Review> optionalReview = ReviewDaoImpl.findLatestReview(uKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalReview.isPresent());
    }

    @Test
    public void findLatestReview_workerId_userId_invalid_workerId_userId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey1 = testInserter.createReview(uKey, uKey2);
        long rKey2 = testInserter.createReview(uKey, uKey2);

        // Exercise
        Optional<Review> optionalReview = ReviewDaoImpl.findLatestReview(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalReview.isPresent());
    }

    @Test
    public void findAverageRating_workerId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        testInserter.createReview(uKey, uKey2, REVIEW_RATING_1, REVIEW_MESSAGE_1, DATE_1);
        testInserter.createReview(uKey, uKey2, REVIEW_RATING_2, REVIEW_MESSAGE_2, DATE_2);

        // Exercise
        Float avgRating = ReviewDaoImpl.findAverageRating(uKey);

        // Validations & Post Conditions
        assertNotNull(avgRating);
        assertEquals((REVIEW_RATING_1 + REVIEW_RATING_2) / 2, avgRating, DELTA); // Average of 4.0 and 4.5
    }

    @Test
    public void findAverageRating_workerId_null() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        Float avgRating = ReviewDaoImpl.findAverageRating(uKey);

        // Validations & Post Conditions
        assertNotNull(avgRating);
        assertEquals(NO_ELEMENTS, avgRating, DELTA);
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(TestConstants.USER_MAIL_2, nhKey); // Reviewer
        long uKey3 = testInserter.createUser(TestConstants.USER_MAIL_3, nhKey); // Reviewer
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey1 = testInserter.createReview(uKey, uKey2);
        long rKey2 = testInserter.createReview(uKey, uKey3);

        // Exercise
        List<Review> reviewList = ReviewDaoImpl.getReviews(uKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, reviewList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey); // Reviewer
        long uKey3 = testInserter.createUser(USER_MAIL_3, nhKey); // Reviewer
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        List<Review> reviewList = ReviewDaoImpl.getReviews(uKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(reviewList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(TestConstants.USER_MAIL_2, nhKey); // Reviewer
        long uKey3 = testInserter.createUser(TestConstants.USER_MAIL_3, nhKey); // Reviewer
        long uKey4 = testInserter.createUser(TestConstants.USER_MAIL_4, nhKey); // Reviewer
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey1 = testInserter.createReview(uKey, uKey2);
        long rKey2 = testInserter.createReview(uKey, uKey3);
        long rKey3 = testInserter.createReview(uKey, uKey4);

        // Exercise
        List<Review> reviewList = ReviewDaoImpl.getReviews(uKey, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, reviewList.size());
    }

    // ------------------------------------------------- COUNTS --------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        testInserter.createReview(uKey, uKey2);
        testInserter.createReview(uKey, uKey2);

        // Exercise
        int countReviews = ReviewDaoImpl.countReviews(uKey);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countReviews);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        int countReviews = ReviewDaoImpl.countReviews(uKey);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countReviews);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_workerId_reviewId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey = testInserter.createReview(uKey, uKey2);

        // Exercise
        boolean deleted = ReviewDaoImpl.deleteReview(uKey, rKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.reviews.name()));
    }

    @Test
    public void delete_workerId_reviewId_invalid_workerId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey = testInserter.createReview(uKey, uKey2);

        // Exercise
        boolean deleted = ReviewDaoImpl.deleteReview(INVALID_ID, rKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_workerId_reviewId_invalid_reviewId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey = testInserter.createReview(uKey, uKey2);

        // Exercise
        boolean deleted = ReviewDaoImpl.deleteReview(nhKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_workerId_reviewId_invalid_workerId_reviewId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long pKey = testInserter.createProfession();
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);
        long rKey = testInserter.createReview(uKey, uKey2);

        // Exercise
        boolean deleted = ReviewDaoImpl.deleteReview(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
