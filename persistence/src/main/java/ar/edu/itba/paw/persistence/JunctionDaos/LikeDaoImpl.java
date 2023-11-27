package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.compositeKeys.LikeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class LikeDaoImpl implements LikeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // -------------------------------------------------- LIKES INSERT -------------------------------------------------

    @Override
    public Like createLike(long postId, long userId) {
        LOGGER.debug("Inserting Like");
        Like like = new Like(em.find(Post.class, postId), em.find(User.class, userId));
        em.persist(like);
        return like;
    }

    // -------------------------------------------------- LIKES SELECT -------------------------------------------------

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
        Long count = (Long) em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.post.postId = :postId AND l.user.userId = :userId")
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .getSingleResult();
        return count != null && count > 0;
    }

    // -------------------------------------------------- LIKES DELETE -------------------------------------------------

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
