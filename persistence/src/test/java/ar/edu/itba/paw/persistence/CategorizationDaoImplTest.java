package ar.edu.itba.paw.persistence;

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
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class CategorizationDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private CategorizationDaoImpl categorizationDao;

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        categorizationDao = new CategorizationDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateCategory() {
        // Pre Conditions
        long chKey = testInsertionUtils.createChannel();
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long pKey = testInsertionUtils.createPost(uKey, chKey, 0);
        long tKey = testInsertionUtils.createTag();

        // Exercise
        categorizationDao.createCategory(tKey, pKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_tags.name()));
    }
}
