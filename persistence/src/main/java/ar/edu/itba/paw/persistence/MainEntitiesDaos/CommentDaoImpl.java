package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.MainEntities.Comment;
import ar.edu.itba.paw.models.MainEntities.Post;
import ar.edu.itba.paw.models.MainEntities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Comment> query = cb.createQuery(Comment.class);
        Root<Comment> commentRoot = query.from(Comment.class);
        query.select(commentRoot);
        query.where(cb.equal(commentRoot.get("post").get("postId"), postId));
        query.orderBy(cb.desc(commentRoot.get("date")));
        TypedQuery<Comment> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);
        List<Comment> comments = typedQuery.getResultList();
        if (comments.isEmpty())
            return Collections.emptyList();
        return comments;
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
