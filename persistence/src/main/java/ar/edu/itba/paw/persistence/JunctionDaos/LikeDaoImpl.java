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
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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
    public List<Like> getLikesByPost(long postId, int page, int size) {
        LOGGER.debug("Selecting Likes from Post {}", postId);
        return em.createQuery("SELECT l FROM Like l WHERE l.post.postId = :postId", Like.class)
                .setParameter("postId", postId)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public List<Like> getLikesByUser(long userId, int page, int size) {
        LOGGER.debug("Selecting Likes from User {}", userId);
        return em.createQuery("SELECT l FROM Like l WHERE l.user.userId = :userId", Like.class)
                .setParameter("userId", userId)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public int getLikesByUserCount(long userId) {
        LOGGER.debug("Selecting Likes from User {}", userId);
        Long count = (Long) em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.user.userId = :userId")
                .setParameter("userId", userId)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public int getLikesByPostCount(long postId) {
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

    @Override
    public Optional<Like> findLikeById(long likeId) {
        LOGGER.debug("Selecting Like with Id {}", likeId);
        return Optional.ofNullable(em.find(Like.class, likeId));
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
