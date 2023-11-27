package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.models.Entities.Resource;
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
public class ResourceDaoImplTest {

    private final String SAMPLE_TITLE = "Sample Title";
    private final String SAMPLE_DESC = "Sample Desc";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ResourceDao resourceDao;

    @PersistenceContext
    private EntityManager em;
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateResource() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        resourceDao.createResource(nhKey, SAMPLE_TITLE, SAMPLE_DESC, 0);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.resources.name()));
    }

    @Test
    public void testGetResources() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long iKey = testInserter.createImage();
        testInserter.createResource(nhKey, iKey);

        // Exercise
        List<Resource> resources = resourceDao.getResources(nhKey);

        // Validations & Post Conditions
        assertEquals(1, resources.size());
    }

    @Test
    public void testGetNoResources() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        List<Resource> resources = resourceDao.getResources(nhKey);

        // Validations & Post Conditions
        assertEquals(0, resources.size());
    }

    @Test
    public void testDeleteResource() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long iKey = testInserter.createImage();
        long rKey = testInserter.createResource(nhKey, iKey);

        // Exercise
        boolean deleted = resourceDao.deleteResource(rKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.resources.name()));
    }

    @Test
    public void testDeleteInvalidResource() {
        // Pre Conditions

        // Exercise
        boolean deleted = resourceDao.deleteResource(1);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
