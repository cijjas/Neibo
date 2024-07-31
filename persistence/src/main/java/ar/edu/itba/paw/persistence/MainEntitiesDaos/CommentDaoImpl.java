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
    public Comment createComment(String commentText, long userId, long postId) {
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
    public Optional<Comment> findComment(long commentId) {
        LOGGER.debug("Selecting Comment with id {}", commentId);

        return Optional.ofNullable(em.find(Comment.class, commentId));
    }

    @Override
    public Optional<Comment> findComment(long commentId, long postId, long neighborhoodId) {
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
    public List<Comment> getComments(long postId, int page, int size) {
        LOGGER.debug("Selecting Comments from Post {}", postId);

        // ID Query to get the commentIds
        TypedQuery<Long> idQuery = em.createQuery("SELECT c.commentId FROM Comment c " +
                "WHERE c.post.postId = :postId ORDER BY c.date, c.commentId", Long.class);
        idQuery.setParameter("postId", postId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> commentIds = idQuery.getResultList();

        if (!commentIds.isEmpty()) {
            // Data Query to get the comments
            TypedQuery<Comment> commentQuery = em.createQuery(
                    "SELECT c FROM Comment c WHERE c.commentId IN :commentIds ORDER BY c.date, c.commentId", Comment.class);
            commentQuery.setParameter("commentIds", commentIds);
            return commentQuery.getResultList();
        }

        return Collections.emptyList();
    }

    @Override
    public int countComments(long id) {
        LOGGER.debug("Selecting Comments Count from Post {}", id);

        Long count = (Long) em.createQuery("SELECT COUNT(c) FROM Comment c " +
                        "WHERE c.post.postId = :postId")
                .setParameter("postId", id)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    // -------------------------------------------- COMMENTS DELETE ----------------------------------------------------

    @Override
    public boolean deleteComment(long commentId) {
        LOGGER.debug("Deleting Comment with id {}", commentId);

        Comment comment = em.find(Comment.class, commentId);
        if (comment != null) {
            em.remove(comment);
            return true;
        }
        return false;
    }
}
