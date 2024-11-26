package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.models.Entities.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentDao commentDao;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Comment createComment(String comment, long user, long postId) {
        LOGGER.info("Creating Comment {} from User {} for Post {}", comment, user, postId);

        return commentDao.createComment(comment, user, postId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findComment(long commentId) {
        LOGGER.info("Finding Comment {}", commentId);

        return commentDao.findComment(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findComment(long commentId, long postId, long neighborhoodId) {
        LOGGER.info("Finding Comment {} in Post {} from Neighborhood {}", commentId, postId, neighborhoodId);

        return commentDao.findComment(commentId, postId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getComments(long postId, int page, int size, long neighborhoodId) {
        LOGGER.info("Getting Comments for Post {} from Neighborhood {}", postId, neighborhoodId);

        return commentDao.getComments(postId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateCommentPages(long postId, int size) {
        LOGGER.info("Calculating Comment for Post {}", postId);

        return PaginationUtils.calculatePages(commentDao.countComments(postId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteComment(long commentId) {
        LOGGER.info("Deleting Comment {}", commentId);

        return commentDao.deleteComment(commentId);
    }
}
