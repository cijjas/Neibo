package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.User;
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
public class CommentDaoImpl implements CommentDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // -------------------------------------------- COMMENTS INSERT ----------------------------------------------------

    @Override
    public Comment createComment(long userId, long postId, String commentText) {
        LOGGER.debug("Inserting Comment {}", commentText);

        Comment comment = new Comment.Builder()
                .comment(commentText)
                .user(em.find(User.class, userId))
                .post(em.find(Post.class, postId))
                .build();
        em.persist(comment);
        return comment;
    }

    // -------------------------------------------- COMMENTS SELECT ----------------------------------------------------

    @Override
    public Optional<Comment> findComment(long neighborhoodId, long postId, long commentId) {
        LOGGER.debug("Selecting Comment with commentId {} and postId {}", commentId, postId);

        TypedQuery<Comment> query = em.createQuery(
                "SELECT c FROM Comment c WHERE c.commentId = :commentId AND c.post.postId = :postId AND c.post.user.neighborhood.id = :neighborhoodId",
                Comment.class
        );

        query.setParameter("commentId", commentId);
        query.setParameter("postId", postId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Comment> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<Comment> findComment(long commentId) {
        LOGGER.debug("Selecting Comment with id {}", commentId);

        return Optional.ofNullable(em.find(Comment.class, commentId));
    }

    @Override
    public List<Comment> getComments(long neighborhoodId, long postId, int page, int size) {
        LOGGER.debug("Selecting Comments from Post {} in Neighborhood {}", postId, neighborhoodId);

        TypedQuery<Long> idQuery = em.createQuery(
                "SELECT c.commentId FROM Comment c " +
                        "WHERE c.post.postId = :postId " +
                        "AND c.user.neighborhood.neighborhoodId = :neighborhoodId " +
                        "ORDER BY c.date DESC, c.commentId",
                Long.class
        );
        idQuery.setParameter("postId", postId);
        idQuery.setParameter("neighborhoodId", neighborhoodId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> commentIds = idQuery.getResultList();

        if (!commentIds.isEmpty()) {
            TypedQuery<Comment> commentQuery = em.createQuery(
                    "SELECT c FROM Comment c " +
                            "WHERE c.commentId IN :commentIds " +
                            "ORDER BY c.date DESC, c.commentId",
                    Comment.class
            );
            commentQuery.setParameter("commentIds", commentIds);
            return commentQuery.getResultList();
        }

        return Collections.emptyList();
    }

    @Override
    public int countComments(long neighborhoodId, long postId) {
        LOGGER.debug("Counting Comments from Post {} in Neighborhood {}", postId, neighborhoodId);

        Long count = (Long) em.createQuery(
                        "SELECT COUNT(c) FROM Comment c " +
                                "WHERE c.post.postId = :postId " +
                                "AND c.user.neighborhood.neighborhoodId = :neighborhoodId"
                )
                .setParameter("postId", postId)
                .setParameter("neighborhoodId", neighborhoodId)
                .getSingleResult();

        return count != null ? count.intValue() : 0;
    }

    // -------------------------------------------- COMMENTS DELETE ----------------------------------------------------

    @Override
    public boolean deleteComment(long neighborhoodId, long postId, long commentId) {
        LOGGER.debug("Deleting Comment with commentId {}, postId {}, and neighborhoodId {}", commentId, postId, neighborhoodId);

        String nativeSql = "DELETE FROM comments c " +
                "WHERE c.commentId = :commentId " +
                "AND c.postId = :postId " +
                "AND c.userId IN (SELECT u.userId FROM users u WHERE u.neighborhoodId = :neighborhoodId)";

        int deletedCount = em.createNativeQuery(nativeSql)
                .setParameter("commentId", commentId)
                .setParameter("postId", postId)
                .setParameter("neighborhoodId", neighborhoodId)
                .executeUpdate();

        return deletedCount > 0;
    }
}
