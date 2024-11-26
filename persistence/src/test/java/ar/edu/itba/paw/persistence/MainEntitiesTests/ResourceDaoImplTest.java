package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Resource;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.ContactDaoImpl;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.ResourceDaoImpl;
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
public class ResourceDaoImplTest {

    private final String RESOURCE_TITLE = "Sample Title";
    private final String RESOURCE_DESCRIPTION = "Sample Desc";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ResourceDaoImpl resourceDaoImpl;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private ContactDaoImpl contactDaoImpl;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Test
    public void create_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long iKey = testInserter.createImage();

        // Exercise
        Resource resource = resourceDaoImpl.createResource(nhKey, RESOURCE_TITLE, RESOURCE_DESCRIPTION, iKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(resource);
        assertEquals(nhKey, resource.getNeighborhood().getNeighborhoodId().longValue());
        assertEquals(iKey, resource.getImage().getImageId().longValue());
        assertEquals(RESOURCE_TITLE, resource.getTitle());
        assertEquals(RESOURCE_DESCRIPTION, resource.getDescription());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.resources.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_resourceId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long iKey = testInserter.createImage();
        long rKey = testInserter.createResource(nhKey, iKey);

        // Exercise
        Optional<Resource> optionalResource = resourceDaoImpl.findResource(rKey);

        // Validations & Post Conditions
        assertTrue(optionalResource.isPresent());
        assertEquals(rKey, optionalResource.get().getResourceId().longValue());
    }

    @Test
    public void find_resourceId_invalid_resourceId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long iKey = testInserter.createImage();
        long rKey = testInserter.createResource(nhKey, iKey);

        // Exercise
        Optional<Resource> optionalResource = resourceDaoImpl.findResource(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalResource.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long iKey = testInserter.createImage();
        testInserter.createResource(nhKey, iKey);

        // Exercise
        List<Resource> resourceList = resourceDaoImpl.getResources(nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, resourceList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        List<Resource> resourceList = resourceDaoImpl.getResources(nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(resourceList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long iKey = testInserter.createImage();
        testInserter.createResource(nhKey, iKey);
        testInserter.createResource(nhKey, iKey);
        testInserter.createResource(nhKey, iKey);

        // Exercise
        List<Resource> resourceList = resourceDaoImpl.getResources(nhKey, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, resourceList.size());
    }

    // ------------------------------------------------- COUNTS --------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long iKey = testInserter.createImage();
        testInserter.createResource(nhKey, iKey);

        // Exercise
        int countResources = resourceDaoImpl.countResources(nhKey);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countResources);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        int countResources = resourceDaoImpl.countResources(nhKey);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countResources);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_resourceId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long iKey = testInserter.createImage();
        long rKey = testInserter.createResource(nhKey, iKey);

        // Exercise
        boolean deleted = resourceDaoImpl.deleteResource(rKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.resources.name()));
    }

    @Test
    public void delete_resourceId_invalid_resourceId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long iKey = testInserter.createImage();
        long rKey = testInserter.createResource(nhKey, iKey);

        // Exercise
        boolean deleted = resourceDaoImpl.deleteResource(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
