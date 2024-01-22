package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.compositeKeys.LikeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
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

        Like like = new Like(em.find(Post.class, postId), em.find(User.class, userId), new java.sql.Date(System.currentTimeMillis()));
        em.persist(like);
        return like;
    }

    // -------------------------------------------------- LIKES SELECT -------------------------------------------------

    @Override
    public List<Like> getLikes(Long postId, Long userId, long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Likes by Criteria");

        TypedQuery<LikeKey> query;

        if (userId != null && postId != null) {
            // Both userId and postId are provided
            query = em.createQuery("SELECT l.id FROM Like l WHERE l.user.userId = :userId AND l.post.postId = :postId", LikeKey.class)
                    .setParameter("userId", userId)
                    .setParameter("postId", postId);
        } else if (userId != null) {
            // Only userId is provided
            query = em.createQuery("SELECT l.id FROM Like l WHERE l.user.userId = :userId", LikeKey.class)
                    .setParameter("userId", userId);
        } else if (postId != null) {
            // Only postId is provided
            query = em.createQuery("SELECT l.id FROM Like l WHERE l.post.postId = :postId", LikeKey.class)
                    .setParameter("postId", postId);
        } else {
            // No specific condition provided, get all likes within a neighborhood
            query = em.createQuery("SELECT l.id FROM Like l WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId", LikeKey.class)
                    .setParameter("neighborhoodId", neighborhoodId);
        }

        query.setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();

        List<LikeKey> likeKeys = query.getResultList();
        if (!likeKeys.isEmpty()) {
            TypedQuery<Like> likeQuery = em.createQuery(
                    "SELECT l FROM Like l WHERE l.id IN :likeKeys", Like.class);
            likeQuery.setParameter("likeKeys", likeKeys);
            return likeQuery.getResultList();
        }
        return Collections.emptyList();
    }


    @Override
    public int countLikes(Long postId, Long userId, long neighborhoodId) {
        LOGGER.debug("Selecting Likes Count by Criteria");

        TypedQuery<Long> query = null;
        if(userId != null) {
            query = em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.user.userId = :userId", Long.class)
                    .setParameter("userId", userId);
        }
        else if(postId != null) {
            query = em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.post.postId = :postId", Long.class)
                    .setParameter("postId", postId);
        }
        else {
            query = em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId", Long.class)
                    .setParameter("neighborhoodId", neighborhoodId);
        }
        Long count = query.getSingleResult();
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
