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
        LOGGER.debug("Inserting Like with User Id {} and Post Id {}", userId, postId);

        Like like = new Like(em.find(Post.class, postId), em.find(User.class, userId), new Date(System.currentTimeMillis()));
        em.persist(like);
        return like;
    }

    // -------------------------------------------------- LIKES SELECT -------------------------------------------------

    @Override
    public List<Like> getLikes(long neighborhoodId, Long userId, Long postId, int page, int size) {
        LOGGER.debug("Selecting Likes for Neighborhood Id {}, User Id {}, and Post Id {}", neighborhoodId, userId, postId);

        TypedQuery<LikeKey> query;

        if (userId != null && postId != null) {
            // Both userId and postId are provided
            query = em.createQuery(
                            "SELECT l.id FROM Like l " +
                                    "WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId " +
                                    "AND l.user.userId = :userId " +
                                    "AND l.post.postId = :postId " +
                                    "ORDER BY l.likeDate",
                            LikeKey.class
                    )
                    .setParameter("neighborhoodId", neighborhoodId)
                    .setParameter("userId", userId)
                    .setParameter("postId", postId);
        } else if (userId != null) {
            // Only userId is provided
            query = em.createQuery(
                            "SELECT l.id FROM Like l " +
                                    "WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId " +
                                    "AND l.user.userId = :userId " +
                                    "ORDER BY l.likeDate",
                            LikeKey.class
                    )
                    .setParameter("neighborhoodId", neighborhoodId)
                    .setParameter("userId", userId);
        } else if (postId != null) {
            // Only postId is provided
            query = em.createQuery(
                            "SELECT l.id FROM Like l " +
                                    "WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId " +
                                    "AND l.post.postId = :postId " +
                                    "ORDER BY l.likeDate",
                            LikeKey.class
                    )
                    .setParameter("neighborhoodId", neighborhoodId)
                    .setParameter("postId", postId);
        } else {
            // No specific condition provided, get all likes within a neighborhood
            query = em.createQuery(
                            "SELECT l.id FROM Like l " +
                                    "WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId " +
                                    "ORDER BY l.likeDate",
                            LikeKey.class
                    )
                    .setParameter("neighborhoodId", neighborhoodId);
        }

        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        List<LikeKey> likeKeys = query.getResultList();

        if (!likeKeys.isEmpty()) {
            TypedQuery<Like> likeQuery = em.createQuery(
                    "SELECT l FROM Like l " +
                            "WHERE l.id IN :likeKeys " +
                            "ORDER BY l.likeDate",
                    Like.class
            );
            likeQuery.setParameter("likeKeys", likeKeys);
            return likeQuery.getResultList();
        }

        return Collections.emptyList();
    }

    @Override
    public int countLikes(long neighborhoodId, Long userId, Long postId) {
        LOGGER.debug("Counting Likes for Neighborhood Id {}, User Id {}, and Post Id {}", neighborhoodId, userId, postId);

        TypedQuery<Long> query;

        if (userId != null && postId != null) {
            query = em.createQuery(
                            "SELECT COUNT(l) FROM Like l " +
                                    "WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId " +
                                    "AND l.user.userId = :userId " +
                                    "AND l.post.postId = :postId",
                            Long.class
                    )
                    .setParameter("neighborhoodId", neighborhoodId)
                    .setParameter("userId", userId)
                    .setParameter("postId", postId);
        } else if (userId != null) {
            query = em.createQuery(
                            "SELECT COUNT(l) FROM Like l " +
                                    "WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId " +
                                    "AND l.user.userId = :userId",
                            Long.class
                    )
                    .setParameter("neighborhoodId", neighborhoodId)
                    .setParameter("userId", userId);
        } else if (postId != null) {
            query = em.createQuery(
                            "SELECT COUNT(l) FROM Like l " +
                                    "WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId " +
                                    "AND l.post.postId = :postId",
                            Long.class
                    )
                    .setParameter("neighborhoodId", neighborhoodId)
                    .setParameter("postId", postId);
        } else {
            query = em.createQuery(
                            "SELECT COUNT(l) FROM Like l " +
                                    "WHERE l.user.neighborhood.neighborhoodId = :neighborhoodId",
                            Long.class
                    )
                    .setParameter("neighborhoodId", neighborhoodId);
        }

        Long count = query.getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    // -------------------------------------------------- LIKES DELETE -------------------------------------------------

    @Override
    public boolean deleteLike(long userId, long postId) {
        LOGGER.debug("Deleting Like with User Id {} and Post Id {}", userId, postId);

        String hql = "DELETE FROM Like l WHERE l.id = :likeId";
        int deletedCount = em.createQuery(hql)
                .setParameter("likeId", new LikeKey(postId, userId))
                .executeUpdate();
        return deletedCount > 0;
    }
}
