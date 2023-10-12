package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.persistence.config.TestConfig;
import enums.Table;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class NeighborhoodWorkerDaoImplTest {

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private NeighborhoodWorkerDao neighborhoodWorkerDao;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        neighborhoodWorkerDao = new NeighborhoodWorkerDaoImpl(ds);
    }

    @Test
    public void testAddWorkerToNeighborhood() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        testInsertionUtils.createWorker(uKey);

        // Exercise
        neighborhoodWorkerDao.addWorkerToNeighborhood(uKey, nhKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_neighborhoods.name()));
    }

    @Test
    public void testRemoveWorkerFromNeighborhood() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        testInsertionUtils.createWorker(uKey);
        neighborhoodWorkerDao.addWorkerToNeighborhood(uKey, nhKey);

        // Exercise
        neighborhoodWorkerDao.removeWorkerFromNeighborhood(uKey, nhKey);

        // Validations & Post Conditions
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_neighborhoods.name()));
    }
}
