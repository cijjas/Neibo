package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SubscriptionDaoImpl implements SubscriptionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public SubscriptionDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("posts_users");
    }

    @Override
    public void createSubscription(long userId, long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", userId);
        data.put("postid", postId);
        jdbcInsert.execute(data);
    }
}
