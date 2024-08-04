package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.TagDaoImpl;
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
public class TagDaoImplTest {

    private final String TAG_NAME_1 = "Tag Name 1";
    private final String TAG_NAME_2 = "Tag Name 2";
    private final String TAG_NAME_3 = "Tag Name 3";
    private final String TAG_NAME_4 = "Tag Name 4";
    private final String TAG_NAME_5 = "Tag Name 5";

    private static long nhKey;
    private static long pKey1;
    private static long pKey2;

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TagDaoImpl tagDaoImpl;

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

        // Exercise
        Tag tag = tagDaoImpl.createTag(TAG_NAME_1);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(tag);
        assertEquals(TAG_NAME_1, tag.getTag());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.tags.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_tagId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey = testInserter.createTag(TAG_NAME_1);
        testInserter.createCategorization(tKey, pKey);

        // Exercise
        Optional<Tag> optionalTag = tagDaoImpl.findTag(TAG_NAME_1);

        // Validations & Post Conditions
        assertTrue(optionalTag.isPresent());
		assertEquals(tKey, optionalTag.get().getTagId().longValue());
    }

    @Test
    public void find_tagId_invalid_tagId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);
        long tKey = testInserter.createTag();
        testInserter.createCategorization(tKey, pKey);

        // Exercise
        Optional<Tag> optionalTag = tagDaoImpl.findTag(INVALID_STRING_ID);

        // Validations & Post Conditions
        assertFalse(optionalTag.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        populateTags();

        // Exercise
        List<Tag> tags = tagDaoImpl.getTags(EMPTY_FIELD, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, tags.size());
    }

    @Test
    public void get_postId() {
        // Pre Conditions
        populateTags();

        // Exercise
        List<Tag> tags = tagDaoImpl.getTags(pKey1, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, tags.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<Tag> tags = tagDaoImpl.getTags(EMPTY_FIELD, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(tags.isEmpty());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        populateTags();

        // Exercise
        int countTags = tagDaoImpl.countTags(EMPTY_FIELD, nhKey);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, countTags);
    }

    @Test
    public void count_postId() {
        // Pre Conditions
        populateTags();

        // Exercise
        int countTags = tagDaoImpl.countTags(pKey1, nhKey);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countTags);
    }

    @Test
    public void count_empty() {
        // Pre Conditions

        // Exercise
        int countTags = tagDaoImpl.countTags(EMPTY_FIELD, nhKey);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countTags);
    }


    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
	public void delete_tagId_valid() {
	    // Pre Conditions
        long tKey1 = testInserter.createTag(TAG_NAME_1);

	    // Exercise
	    boolean deleted = tagDaoImpl.deleteTag(tKey1);

	    // Validations & Post Conditions
		em.flush();
	    assertTrue(deleted);
	    assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.tags.name()));
	}

	@Test
	public void delete_tagId_invalid_tagId() {
        // Pre Conditions
        long tKey1 = testInserter.createTag(TAG_NAME_1);

	    // Exercise
	    boolean deleted = tagDaoImpl.deleteTag(INVALID_ID);

	    // Validations & Post Conditions
		em.flush();
	    assertFalse(deleted);
	}

    // ----------------------------------------------- POPULATION ------------------------------------------------------

    private void populateTags() {

        // [P1]
        // [P1]
        // [P1]
        // [P2]

        long iKey = testInserter.createImage();
        nhKey = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        pKey1 = testInserter.createPost(uKey, chKey, iKey);
        pKey2 = testInserter.createPost(uKey, chKey, iKey);

        long tKey1 = testInserter.createTag(TAG_NAME_1);
        long tKey2 = testInserter.createTag(TAG_NAME_2);
        long tKey3 = testInserter.createTag(TAG_NAME_3);
        long tKey4 = testInserter.createTag(TAG_NAME_4);
        long tKey5 = testInserter.createTag(TAG_NAME_5);

        testInserter.createCategorization(tKey1, pKey1);
        testInserter.createCategorization(tKey2, pKey1);
        testInserter.createCategorization(tKey3, pKey1);
        testInserter.createCategorization(tKey4, pKey2);
        
        testInserter.createTagMapping(nhKey, tKey1);
        testInserter.createTagMapping(nhKey, tKey2);
        testInserter.createTagMapping(nhKey, tKey3);
        testInserter.createTagMapping(nhKey, tKey4);
        testInserter.createTagMapping(nhKey2, tKey5);
    }

}
