package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.hsqldb.jdbc.JDBCDriver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class NeighborhoodDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;
    private NeighborhoodDaoImpl neighborhoodDao;

    private String NEIGHBORHOODS_TABLE = "neighborhoods";
    private String NEIGHBORHOODS_NAME = "Testing Create Neighborhood";

    @Autowired
    private DataSource ds;

    @Before
    public void SetUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        neighborhoodDao = new NeighborhoodDaoImpl(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("neighborhoods")
                .usingGeneratedKeyColumns("neighborhoodid");
    }

    @Test
    public void testCreateNeighborhood() {
        // Pre Conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, NEIGHBORHOODS_TABLE);

        // Exercise
        Neighborhood nh = neighborhoodDao.createNeighborhood(NEIGHBORHOODS_NAME);

        // Validations & Post Conditions
        assertNotNull(nh);
        assertEquals(NEIGHBORHOODS_NAME, nh.getName());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, NEIGHBORHOODS_TABLE));
    }

    @Test
    public void testFindByNeighborhoodByInvalidId(){
        // Pre Conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, NEIGHBORHOODS_TABLE);

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhoodById(1);

        // Validations & Post Conditions
        assertFalse(nh.isPresent());
    }

    @Test
    public void testFindByNeighborhoodByValidId(){
        // Pre Conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, NEIGHBORHOODS_TABLE);
        Map<String, Object> data = new HashMap<>();
        data.put("neighborhoodname", NEIGHBORHOODS_NAME);
        Number key = jdbcInsert.executeAndReturnKey(data);

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhoodById(key.longValue());

        // Validations & Post Conditions
        assertTrue(nh.isPresent());
        assertEquals(NEIGHBORHOODS_NAME, nh.get().getName());
    }
}
