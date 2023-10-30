package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.compositeKeys.ChannelMappingKey;
import ar.edu.itba.paw.compositeKeys.LikeKey;
import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.models.JunctionEntities.ChannelMapping;
import ar.edu.itba.paw.models.JunctionEntities.Like;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class LikeDaoImpl implements LikeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String COUNT_LIKES = "SELECT COUNT(*) FROM posts_users_likes";

    @Autowired
    public LikeDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("posts_users_likes");
    }

    // ---------------------------------------------- POST_USERS_LIKES INSERT ------------------------------------------

    @Override
    public Like createLike(long postId, long userId) {
        LOGGER.debug("Inserting Like");
        Like like = new Like(em.find(Post.class, postId), em.find(User.class, userId));
        em.persist(like);
        return like;
    }
    // ---------------------------------------------- POST_USERS_LIKES SELECT ------------------------------------------

    @Override
    public int getLikes(long postId) {
        LOGGER.debug("Selecting Likes from Post {}", postId);
        Long count = (Long) em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.post.postId = :postId")
                .setParameter("postId", postId)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public boolean isPostLiked(long postId, long userId) {
        LOGGER.debug("Selecting Likes from Post {} and userId {}", postId, userId);
        // Check if a like exists for the given post and user
        Long count = (Long) em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.post.postId = :postId AND l.user.userId = :userId")
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .getSingleResult();
        return count != null && count > 0;
    }

    // ---------------------------------------------- POST_USERS_LIKES DELETE ------------------------------------------

    @Override
    public boolean deleteLike(long postId, long userId) {
        LOGGER.debug("Deleting Like from Post {} and userId {}", postId, userId);
        Like like = em.find(Like.class, new LikeKey(postId, userId));
        if (like != null) {
            em.remove(like);
            return true;
        }
        return false;

    }
}
