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
import javax.persistence.TypedQuery;
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
    public List<Like> getLikesByCriteria(long postId, long userId, long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Likes by Criteria");
        TypedQuery<Like> query = null;

        if(userId > 0) {
            query = em.createQuery("SELECT l FROM Like l WHERE l.user.userId = :userId", Like.class)
                    .setParameter("userId", userId);
        }
        else if(postId > 0) {
            query = em.createQuery("SELECT l FROM Like l WHERE l.post.postId = :postId", Like.class)
                    .setParameter("postId", postId);
        }
        else {
            //get all likes (within a neighborhood)
            query = em.createQuery("SELECT l FROM Like l WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId", Like.class)
                    .setParameter("neighborhoodId", neighborhoodId);
        }
        return query.setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public int getLikesCountByCriteria(long postId, long userId, long neighborhoodId) {
        LOGGER.debug("Selecting Likes Count by Criteria");
        TypedQuery<Long> query = null;
        if(userId > 0) {
            query = em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.user.userId = :userId", Long.class)
                    .setParameter("userId", userId);
        }
        else if(postId > 0) {
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
