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
    public Optional<Comment> findCommentById(long commentId) {
        LOGGER.debug("Selecting Comments with commentId {}", commentId);
        Comment comment = em.find(Comment.class, commentId);
        return Optional.ofNullable(comment);
    }

    @Override
    public List<Comment> getCommentsByPostId(long postId, int page, int size) {
        LOGGER.debug("Selecting Comments from Post {}", postId);

        TypedQuery<Long> idQuery = em.createQuery("SELECT c.commentId FROM Comment c " +
                "WHERE c.post.postId = :postId", Long.class);
        idQuery.setParameter("postId", postId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> commentIds = idQuery.getResultList();

        if (!commentIds.isEmpty()) {
            TypedQuery<Comment> commentQuery = em.createQuery(
                    "SELECT c FROM Comment c WHERE c.commentId IN :commentIds", Comment.class);
            commentQuery.setParameter("commentIds", commentIds);
            return commentQuery.getResultList();
        }

        return Collections.emptyList();
    }

    @Override
    public int getCommentsCountByPostId(long id) {
        LOGGER.debug("Selecting Comments Count from Post {}", id);
        Long count = (Long) em.createQuery("SELECT COUNT(c) FROM Comment c " +
                        "WHERE c.post.postId = :postId")
                .setParameter("postId", id)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }
}
