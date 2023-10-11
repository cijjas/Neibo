package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
import ar.edu.itba.paw.persistence.SubscriptionDaoImpl;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class SubscriptionDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private SubscriptionDao subscriptionDao;

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        subscriptionDao = new SubscriptionDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateSubscription() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long chKey = testInsertionUtils.createChannel();
        long iKey = testInsertionUtils.createImage();
        long pKey = testInsertionUtils.createPost(uKey, chKey, iKey);

        // Exercise
        subscriptionDao.createSubscription(uKey, pKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_subscriptions.name()));
    }
}
