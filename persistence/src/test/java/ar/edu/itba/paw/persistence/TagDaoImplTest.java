package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.persistence.TagDaoImpl;
import ar.edu.itba.paw.persistence.Table;
import ar.edu.itba.paw.persistence.TestInsertionUtils;
import ar.edu.itba.paw.persistence.config.TestConfig;
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
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class TagDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private TagDaoImpl tagDao;

    private String TAG_NAME = "Sample Tag";

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        tagDao = new TagDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateTag() {
        // Pre Conditions

        // Exercise
        Tag ch = tagDao.createTag(TAG_NAME);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.tags.name()));
        assertEquals(TAG_NAME, ch.getTag());
    }

    @Test
    public void testFindTagsByPostId() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long chKey = testInsertionUtils.createChannel();
        long pKey = testInsertionUtils.createPost(uKey, chKey, 0);
        long tKey = testInsertionUtils.createTag();
        testInsertionUtils.createCategorization(tKey, pKey);

        // Exercise
        List<Tag> tags = tagDao.findTagsByPostId(pKey);

        // Validations & Post Conditions
        assertFalse(tags.isEmpty());
        assertEquals(1, tags.size());
    }

    @Test
    public void testFindNoTagsByPostId() {
        // Pre Conditions

        // Exercise
        List<Tag> tags = tagDao.findTagsByPostId(1);

        // Validations & Post Conditions
        assertTrue(tags.isEmpty());
    }

    @Test
    public void testGetTags() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long chKey = testInsertionUtils.createChannel();
        long pKey = testInsertionUtils.createPost(uKey, chKey, 0);
        long tKey = testInsertionUtils.createTag();
        testInsertionUtils.createCategorization(tKey, pKey);

        // Exercise
        List<Tag> tags = tagDao.getTags(nhKey);

        // Validations & Post Conditions
        assertEquals(1, tags.size());
    }

    @Test
    public void testGetNoTags() {
        // Pre Conditions

        // Exercise
        List<Tag> tags = tagDao.getTags(1);

        // Validations & Post Conditions
        assertEquals(0, tags.size());
    }

    @Test
    public void testGetAllTags() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long chKey = testInsertionUtils.createChannel();
        long pKey = testInsertionUtils.createPost(uKey, chKey, 0);
        long tKey1 = testInsertionUtils.createTag("Tag 1");
        long tKey2 = testInsertionUtils.createTag("Tag 2");
        long tKey3 = testInsertionUtils.createTag("Tag 3");
        testInsertionUtils.createCategorization(tKey1, pKey);
        testInsertionUtils.createCategorization(tKey2, pKey);
        testInsertionUtils.createCategorization(tKey3, pKey);

        // Exercise
        List<Tag> tags = tagDao.getAllTags();

        // Validations & Post Conditions
        assertEquals(3, tags.size());
    }
}
