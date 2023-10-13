package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
import ar.edu.itba.paw.models.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class LikeDaoImpl implements LikeDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final Logger LOGGER = LoggerFactory.getLogger(LikeDaoImpl.class);

    @Autowired
    public LikeDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("posts_users_likes");
    }

    // ---------------------------------------------- POST_USERS_LIKES INSERT ------------------------------------------

    @Override
    public void createLike(long postId, long userId) {
        LOGGER.info("Inserting Like");
        Map<String, Object> data = new HashMap<>();
        data.put("postid", postId);
        data.put("likedate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("userid", userId);

        try {
            jdbcInsert.execute(data);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Like", ex);
            throw new InsertionException("An error occurred whilst liking the post");
        }
    }
    // ---------------------------------------------- POST_USERS_LIKES SELECT ------------------------------------------

    public int getLikes(long postId) {
        LOGGER.info("Selecting Likes from Post {}", postId);
        String sql = "SELECT COUNT(*) FROM posts_users_likes WHERE postid = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, postId);
    }

    @Override
    public boolean isPostLiked(long postId, long userId) {
        LOGGER.info("Selecting Likes from Post {} and userId {}", postId, userId);
        String sql = "SELECT COUNT(*) FROM posts_users_likes WHERE postid = ? AND userid = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, postId, userId) > 0;
    }

    // ---------------------------------------------- POST_USERS_LIKES DELETE ------------------------------------------

    @Override
    public boolean deleteLike(long postId, long userId){
        LOGGER.info("Deleting Like from Post {} and userId {}", postId, userId);
        return jdbcTemplate.update("DELETE FROM posts_users_likes WHERE postid = ? AND userid = ? ", postId, userId) > 0;
    }
}
