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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class TagDaoImplTest {

    private final String TAG_NAME = "Sample Tag";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TagDaoImpl tagDao;

    @PersistenceContext
    private EntityManager em;
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateTag() {
        // Pre Conditions

        // Exercise
        Tag ch = tagDao.createTag(TAG_NAME);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.tags.name()));
        assertEquals(TAG_NAME, ch.getTag());
    }

    @Test
    public void testFindTagsByPostId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long pKey = testInserter.createPost(uKey, chKey, 0);
        long tKey = testInserter.createTag();
        testInserter.createCategorization(tKey, pKey);

        // Exercise
        List<Tag> tags = tagDao.getTags(pKey);

        // Validations & Post Conditions
        assertFalse(tags.isEmpty());
        assertEquals(1, tags.size());
    }

    @Test
    public void testFindNoTagsByPostId() {
        // Pre Conditions

        // Exercise
        List<Tag> tags = tagDao.getTags(1);

        // Validations & Post Conditions
        assertTrue(tags.isEmpty());
    }

    @Test
    public void testGetTags() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long pKey = testInserter.createPost(uKey, chKey, 0);
        long tKey = testInserter.createTag();
        testInserter.createCategorization(tKey, pKey);

        // Exercise
        List<Tag> tags = tagDao.getNeighborhoodTags(nhKey);

        // Validations & Post Conditions
        assertEquals(1, tags.size());
    }

    @Test
    public void testGetNoTags() {
        // Pre Conditions

        // Exercise
        List<Tag> tags = tagDao.getNeighborhoodTags(1);

        // Validations & Post Conditions
        assertEquals(0, tags.size());
    }

    @Test
    public void testGetAllTags() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long pKey = testInserter.createPost(uKey, chKey, 0);
        long tKey1 = testInserter.createTag("Tag 1");
        long tKey2 = testInserter.createTag("Tag 2");
        long tKey3 = testInserter.createTag("Tag 3");
        testInserter.createCategorization(tKey1, pKey);
        testInserter.createCategorization(tKey2, pKey);
        testInserter.createCategorization(tKey3, pKey);

        // Exercise
        List<Tag> tags = tagDao.getAllTags();

        // Validations & Post Conditions
        assertEquals(3, tags.size());
    }
}
