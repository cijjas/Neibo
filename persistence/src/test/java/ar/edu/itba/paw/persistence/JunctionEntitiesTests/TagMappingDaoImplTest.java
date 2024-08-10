package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.TagMapping;
import ar.edu.itba.paw.persistence.JunctionDaos.TagMappingDaoImpl;
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
public class TagMappingDaoImplTest {
    public static final String TAG_NAME_1 = "Tag Name 1";
    public static final String TAG_NAME_2 = "Tag Name 2";
    public static final String TAG_NAME_3 = "Tag Name 3";
    public static final String TAG_NAME_4 = "Tag Name 4";
    private static long nhKey1;
    private static long nhKey2;
    private static long tKey1;
    private static long tKey2;
    private static long tKey3;
    private static long tKey4;
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TagMappingDaoImpl tagMappingDaoImpl;
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
        long tKey = testInserter.createTag();

        // Exercise
        TagMapping tagMapping = tagMappingDaoImpl.createTagMappingDao(tKey, nhKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(tagMapping);
        assertEquals(tKey, tagMapping.getTag().getTagId().longValue());
        assertEquals(nhKey, tagMapping.getNeighborhood().getNeighborhoodId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods_tags.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

	@Test
	public void find_tagId_neighborhoodId_valid() {
	    // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        tKey1 = testInserter.createTag(TAG_NAME_1);
        testInserter.createTagMapping(nhKey1, tKey1);

	    // Exercise
	    Optional<TagMapping> optionalTagMapping = tagMappingDaoImpl.findTagMapping(tKey1, nhKey1);

	    // Validations & Post Conditions
	    assertTrue(optionalTagMapping.isPresent());
		assertEquals(tKey1, optionalTagMapping.get().getTag().getTagId().longValue());
        assertEquals(nhKey1, optionalTagMapping.get().getNeighborhood().getNeighborhoodId().longValue());
	}

    @Test
    public void find_tagId_neighborhoodId_invalid_tagId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        tKey1 = testInserter.createTag(TAG_NAME_1);
        testInserter.createTagMapping(nhKey1, tKey1);

        // Exercise
        Optional<TagMapping> optionalTagMapping = tagMappingDaoImpl.findTagMapping(INVALID_ID, nhKey1);

        // Validations & Post Conditions
        assertFalse(optionalTagMapping.isPresent());
    }

    @Test
    public void find_tagId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        tKey1 = testInserter.createTag(TAG_NAME_1);
        testInserter.createTagMapping(nhKey1, tKey1);

        // Exercise
        Optional<TagMapping> optionalTagMapping = tagMappingDaoImpl.findTagMapping(tKey1, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalTagMapping.isPresent());
    }

    @Test
    public void find_tagId_neighborhoodId_invalid_tagId_neighborhoodId() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        tKey1 = testInserter.createTag(TAG_NAME_1);
        testInserter.createTagMapping(nhKey1, tKey1);

        // Exercise
        Optional<TagMapping> optionalTagMapping = tagMappingDaoImpl.findTagMapping(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalTagMapping.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        populateTagMappings();

        // Exercise
        List<TagMapping> tagMappingsList = tagMappingDaoImpl.getTagMappings(EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(FIVE_ELEMENTS, tagMappingsList.size());
    }

    @Test
    public void get_tagId() {
        // Pre Conditions
        populateTagMappings();

        // Exercise
        List<TagMapping> tagMappingsList = tagMappingDaoImpl.getTagMappings(tKey2, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, tagMappingsList.size());
    }

    @Test
    public void get_neighborhoodId() {
        // Pre Conditions
        populateTagMappings();

        // Exercise
        List<TagMapping> tagMappingsList = tagMappingDaoImpl.getTagMappings(EMPTY_FIELD, nhKey2, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, tagMappingsList.size());
    }

    @Test
    public void get_tagId_neighborhoodId() {
        // Pre Conditions
        populateTagMappings();

        // Exercise
        List<TagMapping> tagMappingsList = tagMappingDaoImpl.getTagMappings(tKey1, nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, tagMappingsList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long tKey1 = testInserter.createTag(TAG_NAME_1);
        long tKey2 = testInserter.createTag(TAG_NAME_2);
        long tKey3 = testInserter.createTag(TAG_NAME_3);

        // Exercise
        List<TagMapping> tagMappingsList = tagMappingDaoImpl.getTagMappings(EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(tagMappingsList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        tKey1 = testInserter.createTag(TAG_NAME_1);
        tKey2 = testInserter.createTag(TAG_NAME_2);
        tKey3 = testInserter.createTag(TAG_NAME_3);
        testInserter.createTagMapping(nhKey1, tKey1);
        testInserter.createTagMapping(nhKey1, tKey2);
        testInserter.createTagMapping(nhKey2, tKey2);

        // Exercise
        List<TagMapping> tagMappingsList = tagMappingDaoImpl.getTagMappings(EMPTY_FIELD, EMPTY_FIELD, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, tagMappingsList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        populateTagMappings();

        // Exercise
        int countLikes = tagMappingDaoImpl.countTagMappings(EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(FIVE_ELEMENTS, countLikes);
    }

    @Test
    public void count_tagId() {
        // Pre Conditions
        populateTagMappings();

        // Exercise
        int countLikes = tagMappingDaoImpl.countTagMappings(tKey2, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countLikes);
    }

    @Test
    public void count_neighborhoodId() {
        // Pre Conditions
        populateTagMappings();

        // Exercise
        int countLikes = tagMappingDaoImpl.countTagMappings(EMPTY_FIELD, nhKey2);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countLikes);
    }

    @Test
    public void count_tagId_neighborhoodId() {
        // Pre Conditions
        populateTagMappings();

        // Exercise
        int countLikes = tagMappingDaoImpl.countTagMappings(tKey1, nhKey1);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countLikes);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long tKey1 = testInserter.createTag(TAG_NAME_1);
        long tKey2 = testInserter.createTag(TAG_NAME_2);
        long tKey3 = testInserter.createTag(TAG_NAME_3);

        // Exercise
        int countLikes = tagMappingDaoImpl.countTagMappings(EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countLikes);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_tagId_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long tKey1 = testInserter.createTag(TAG_NAME_1);
        long tKey2 = testInserter.createTag(TAG_NAME_2);
        long tKey3 = testInserter.createTag(TAG_NAME_3);
        testInserter.createTagMapping(nhKey1, tKey1);

        // Exercise
        boolean deleted = tagMappingDaoImpl.deleteTagMapping(tKey1, nhKey1);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods_tags.name()));
    }

    @Test
    public void delete_tagId_neighborhoodId_invalid_tagId() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long tKey1 = testInserter.createTag(TAG_NAME_1);
        long tKey2 = testInserter.createTag(TAG_NAME_2);
        long tKey3 = testInserter.createTag(TAG_NAME_3);
        testInserter.createTagMapping(nhKey1, tKey1);

        // Exercise
        boolean deleted = tagMappingDaoImpl.deleteTagMapping(INVALID_ID, nhKey1);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_tagId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long tKey1 = testInserter.createTag(TAG_NAME_1);
        long tKey2 = testInserter.createTag(TAG_NAME_2);
        long tKey3 = testInserter.createTag(TAG_NAME_3);
        testInserter.createTagMapping(nhKey1, tKey1);

        // Exercise
        boolean deleted = tagMappingDaoImpl.deleteTagMapping(tKey1, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_tagId_neighborhoodId_invalid_tagId_neighborhoodId() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long tKey1 = testInserter.createTag(TAG_NAME_1);
        long tKey2 = testInserter.createTag(TAG_NAME_2);
        long tKey3 = testInserter.createTag(TAG_NAME_3);
        testInserter.createTagMapping(nhKey1, tKey1);

        // Exercise
        boolean deleted = tagMappingDaoImpl.deleteTagMapping(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    // ----------------------------------------------- POPULATION ------------------------------------------------------

    private void populateTagMappings() {
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        tKey1 = testInserter.createTag(TAG_NAME_1);
        tKey2 = testInserter.createTag(TAG_NAME_2);
        tKey3 = testInserter.createTag(TAG_NAME_3);
        tKey4 = testInserter.createTag(TAG_NAME_4);
        testInserter.createTagMapping(nhKey1, tKey1);
        testInserter.createTagMapping(nhKey1, tKey2);
        testInserter.createTagMapping(nhKey2, tKey2);
        testInserter.createTagMapping(nhKey2, tKey3);
        testInserter.createTagMapping(nhKey2, tKey4);
    }
}
