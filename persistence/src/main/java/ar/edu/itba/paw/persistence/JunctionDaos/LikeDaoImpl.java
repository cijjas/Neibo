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
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Repository
public class LikeDaoImpl implements LikeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // -------------------------------------------------- LIKES INSERT -------------------------------------------------

    @Override
    public Like createLike(long userId, long postId) {
        LOGGER.debug("Inserting Like");

        Like like = new Like(em.find(Post.class, postId), em.find(User.class, userId), new Date(System.currentTimeMillis()));
        em.persist(like);
        return like;
    }

    // -------------------------------------------------- LIKES SELECT -------------------------------------------------

    @Override
    public List<Like> getLikes(Long userId, Long postId, int page, int size) {
        LOGGER.debug("Selecting Likes by Criteria");

        // Create a TypedQuery for LikeKey based on the provided criteria
        TypedQuery<LikeKey> query;

        if (userId != null && postId != null) {
            // Both userId and postId are provided
            query = em.createQuery("SELECT l.id FROM Like l WHERE l.user.userId = :userId AND l.post.postId = :postId ORDER BY l.likeDate", LikeKey.class)
                    .setParameter("userId", userId)
                    .setParameter("postId", postId);
        } else if (userId != null) {
            // Only userId is provided
            query = em.createQuery("SELECT l.id FROM Like l WHERE l.user.userId = :userId ORDER BY l.likeDate", LikeKey.class)
                    .setParameter("userId", userId);
        } else if (postId != null) {
            // Only postId is provided
            query = em.createQuery("SELECT l.id FROM Like l WHERE l.post.postId = :postId ORDER BY l.likeDate", LikeKey.class)
                    .setParameter("postId", postId);
        } else {
            // No specific condition provided, get all likes within a neighborhood
            query = em.createQuery("SELECT l.id FROM Like l ORDER BY l.likeDate", LikeKey.class);
        }

        // Set pagination parameters
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        // Get the list of LikeKeys
        List<LikeKey> likeKeys = query.getResultList();

        // Check if the list is empty
        if (!likeKeys.isEmpty()) {
            // Create a TypedQuery for Like based on the LikeKeys and order by likeDate
            TypedQuery<Like> likeQuery = em.createQuery(
                    "SELECT l FROM Like l WHERE l.id IN :likeKeys ORDER BY l.likeDate", Like.class);
            likeQuery.setParameter("likeKeys", likeKeys);
            return likeQuery.getResultList();
        }

        return Collections.emptyList();
    }

    @Override
    public int countLikes(Long userId, Long postId) {
        LOGGER.debug("Selecting Likes Count by Criteria");

        TypedQuery<Long> query = null;
        if(userId != null && postId != null) {
            query = em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.user.userId = :userId AND l.post.postId = :postId", Long.class)
                    .setParameter("userId", userId)
                    .setParameter("postId", postId);
        } else if (userId != null) {
            query = em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.user.userId = :userId", Long.class)
                    .setParameter("userId", userId);
        } else if (postId != null) {
            query = em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.post.postId = :postId", Long.class)
                    .setParameter("postId", postId);
        } else {
            query = em.createQuery("SELECT COUNT(l) FROM Like l", Long.class);
        }
        Long count = query.getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    // -------------------------------------------------- LIKES DELETE -------------------------------------------------

    @Override
    public boolean deleteLike(long userId, long postId) {
        LOGGER.debug("Deleting Like from Post {} and userId {}", postId, userId);

        String hql = "DELETE FROM Like l WHERE l.id = :likeId";
        int deletedCount = em.createQuery(hql)
                .setParameter("likeId", new LikeKey(postId, userId))
                .executeUpdate();
        return deletedCount > 0;
    }
}
