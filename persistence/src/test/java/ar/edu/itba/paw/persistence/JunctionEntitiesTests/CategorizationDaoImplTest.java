package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.persistence.JunctionDaos.CategorizationDaoImpl;
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

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class CategorizationDaoImplTest {

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    private CategorizationDaoImpl categorizationDao;


    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        categorizationDao = new CategorizationDaoImpl(ds);
    }

    @Test
    public void testCreateCategory() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long pKey = testInserter.createPost(uKey, chKey, 0);
        long tKey = testInserter.createTag();

        // Exercise
        categorizationDao.createCategory(tKey, pKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_tags.name()));
    }
}
