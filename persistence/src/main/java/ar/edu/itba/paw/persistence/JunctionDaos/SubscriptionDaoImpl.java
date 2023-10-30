package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
import ar.edu.itba.paw.models.JunctionEntities.Subscription;
import ar.edu.itba.paw.models.MainEntities.Post;
import ar.edu.itba.paw.models.MainEntities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SubscriptionDaoImpl implements SubscriptionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public SubscriptionDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("posts_users_subscriptions");
    }

    // ---------------------------------------------- POST_USERS_SUBSCRIPTIONS INSERT ----------------------------------

    @Override
    public Subscription createSubscription(long userId, long postId) {
        LOGGER.debug("Inserting Subscription");
        Subscription subscription = new Subscription(em.find(Post.class, postId), em.find(User.class, userId));
        em.persist(subscription);
        return subscription;
    }
}
