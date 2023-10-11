package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.jdbc.Sql;

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
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        testInsertionUtils.createWorker(uKey.longValue());

        // Exercise
        neighborhoodWorkerDao.addWorkerToNeighborhood(uKey.longValue(), nhKey.longValue());

        // Validations & Post Conditions
        // No exception should be thrown
    }

    @Test
    public void testRemoveWorkerFromNeighborhood() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        testInsertionUtils.createWorker(uKey.longValue());
        neighborhoodWorkerDao.addWorkerToNeighborhood(uKey.longValue(), nhKey.longValue());

        // Exercise
        neighborhoodWorkerDao.removeWorkerFromNeighborhood(uKey.longValue(), nhKey.longValue());

        // Validations & Post Conditions
        // No exception should be thrown
    }
}
