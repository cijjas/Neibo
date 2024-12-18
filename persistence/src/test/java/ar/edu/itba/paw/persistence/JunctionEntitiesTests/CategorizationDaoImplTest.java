package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Categorization;
import ar.edu.itba.paw.persistence.JunctionDaos.CategorizationDaoImpl;
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
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class CategorizationDaoImplTest {

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CategorizationDaoImpl categorizationDaoImpl;
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
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey = testInserter.createTag();

        // Exercise
        Categorization categorization = categorizationDaoImpl.createCategorization(pKey, tKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(categorization);
        assertEquals(tKey, categorization.getTag().getTagId().longValue());
        assertEquals(pKey, categorization.getPost().getPostId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_tags.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_tagId_postId_valid() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood();
        long chKey = testInserter.createChannel();
        long uKey = testInserter.createUser(nhKey1);
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey1 = testInserter.createTag();
        testInserter.createCategorization(tKey1, pKey);

        // Exercise
        Optional<Categorization> optionalCategorization = categorizationDaoImpl.findCategorization(pKey, tKey1);

        // Validations & Post Conditions
        assertTrue(optionalCategorization.isPresent());
        assertEquals(tKey1, optionalCategorization.get().getTag().getTagId().longValue());
        assertEquals(pKey, optionalCategorization.get().getPost().getPostId().longValue());
    }

    @Test
    public void find_tagId_postId_invalid_tagId() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood();
        long chKey = testInserter.createChannel();
        long uKey = testInserter.createUser(nhKey1);
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey1 = testInserter.createTag();
        testInserter.createCategorization(tKey1, pKey);

        // Exercise
        Optional<Categorization> optionalCategorization = categorizationDaoImpl.findCategorization(pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalCategorization.isPresent());
    }

    @Test
    public void find_tagId_postId_invalid_postId() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood();
        long chKey = testInserter.createChannel();
        long uKey = testInserter.createUser(nhKey1);
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey1 = testInserter.createTag();
        testInserter.createCategorization(tKey1, pKey);

        // Exercise
        Optional<Categorization> optionalCategorization = categorizationDaoImpl.findCategorization(INVALID_ID, tKey1);

        // Validations & Post Conditions
        assertFalse(optionalCategorization.isPresent());
    }

    @Test
    public void find_tagId_postId_invalid_tagId_postId() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood();
        long chKey = testInserter.createChannel();
        long uKey = testInserter.createUser(nhKey1);
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey1 = testInserter.createTag();
        testInserter.createCategorization(tKey1, pKey);

        // Exercise
        Optional<Categorization> optionalCategorization = categorizationDaoImpl.findCategorization(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalCategorization.isPresent());
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_tagId_postId_valid() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood();
        long chKey = testInserter.createChannel();
        long uKey = testInserter.createUser(nhKey1);
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey1 = testInserter.createTag();
        testInserter.createCategorization(tKey1, pKey);

        // Exercise
        boolean deleted = categorizationDaoImpl.deleteCategorization(pKey, tKey1);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_tags.name()));
    }

    @Test
    public void delete_tagId_postId_invalid_tagId() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood();
        long chKey = testInserter.createChannel();
        long uKey = testInserter.createUser(nhKey1);
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey1 = testInserter.createTag();
        testInserter.createCategorization(tKey1, pKey);

        // Exercise
        boolean deleted = categorizationDaoImpl.deleteCategorization(pKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_tagId_postId_invalid_postId() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood();
        long chKey = testInserter.createChannel();
        long uKey = testInserter.createUser(nhKey1);
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey1 = testInserter.createTag();
        testInserter.createCategorization(tKey1, pKey);

        // Exercise
        boolean deleted = categorizationDaoImpl.deleteCategorization(INVALID_ID, tKey1);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_tagId_postId_invalid_tagId_postId() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood();
        long chKey = testInserter.createChannel();
        long uKey = testInserter.createUser(nhKey1);
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey1 = testInserter.createTag();
        testInserter.createCategorization(tKey1, pKey);

        // Exercise
        boolean deleted = categorizationDaoImpl.deleteCategorization(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
