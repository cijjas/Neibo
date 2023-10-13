package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SubscriptionDaoImpl implements SubscriptionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionDaoImpl.class);

    @Autowired
    public SubscriptionDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("posts_users_subscriptions");
    }

    // ---------------------------------------------- POST_USERS_SUBSCRIPTIONS INSERT ----------------------------------

    @Override
    public void createSubscription(long userId, long postId) {
        LOGGER.info("Inserting Subscription");
        Map<String, Object> data = new HashMap<>();
        data.put("userid", userId);
        data.put("postid", postId);
        try {
            jdbcInsert.execute(data);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Subscription", ex);
            throw new InsertionException("An error occurred whilst creating the Subscription");
        }
    }
}
