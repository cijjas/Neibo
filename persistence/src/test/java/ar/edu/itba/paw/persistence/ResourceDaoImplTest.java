package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.models.Resource;
import ar.edu.itba.paw.persistence.ResourceDaoImpl;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class ResourceDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private ResourceDao resourceDao;

    private String SAMPLE_TITLE = "Sample Title";
    private String SAMPLE_DESC = "Sample Desc";

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        resourceDao = new ResourceDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateResource() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();

        // Exercise
        resourceDao.createResource(nhKey, SAMPLE_TITLE, SAMPLE_DESC, 0);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.resources.name()));
    }

    @Test
    public void testGetResources() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long iKey = testInsertionUtils.createImage();
        testInsertionUtils.createResource(nhKey, iKey);

        // Exercise
        List<Resource> resources = resourceDao.getResources(nhKey);

        // Validations & Post Conditions
        assertEquals(1, resources.size());
    }

    @Test
    public void testGetNoResources() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();

        // Exercise
        List<Resource> resources = resourceDao.getResources(nhKey);

        // Validations & Post Conditions
        assertEquals(0, resources.size());
    }

    @Test
    public void testDeleteResource() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long iKey = testInsertionUtils.createImage();
        long rKey = testInsertionUtils.createResource(nhKey, iKey);

        // Exercise
        boolean deleted = resourceDao.deleteResource(rKey);

        // Validations & Post Conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.resources.name()));
    }

    @Test
    public void testDeleteInvalidResource() {
        // Pre Conditions

        // Exercise
        boolean deleted = resourceDao.deleteResource(1);

        // Validations & Post Conditions
        assertFalse(deleted);
    }
}
