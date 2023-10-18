package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Sql("classpath:hsqlValueCleanUp.sql")
public class SubscriptionDaoImplTest {

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    private SubscriptionDao subscriptionDao;


    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        subscriptionDao = new SubscriptionDaoImpl(ds);
    }

    @Test
    public void testCreateSubscription() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long chKey = testInserter.createChannel();
        long iKey = testInserter.createImage();
        long pKey = testInserter.createPost(uKey, chKey, iKey);

        // Exercise
        subscriptionDao.createSubscription(uKey, pKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_subscriptions.name()));
    }
}
