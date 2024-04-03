package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.models.Entities.Post;
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
    private final EmailService emailService;
    private final PostDao postDao;
    private final UserService userService;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao, EmailService emailService, PostDao postDao, UserService userService) {
        this.commentDao = commentDao;
        this.emailService = emailService;
        this.postDao = postDao;
        this.userService = userService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Comment createComment(String comment, long userId, long postId) {
        LOGGER.info("Creating Comment {} from User {} for Post {}", comment, userId, postId);

        Post post = postDao.findPost(postId).orElseThrow(()-> new NotFoundException("Post Not Found"));
        emailService.sendNewCommentMail(post, userService.getNeighborsSubscribed(postId));
        return commentDao.createComment(comment, userId, postId);
    }

    @Override
    public boolean deleteComment(long commentId) {
        LOGGER.info("Deleting Comment {}", commentId);

        return commentDao.deleteComment(commentId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findComment(long commentId) {
        LOGGER.info("Finding Comment {}", commentId);

        ValidationUtils.checkCommentId(commentId);

        return commentDao.findComment(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findComment(long commentId, long postId, long neighborhoodId) {
        LOGGER.info("Finding Comment {} in Post {} from Neighborhood {}", commentId, postId, neighborhoodId);

        ValidationUtils.checkCommentId(commentId);
        ValidationUtils.checkPostId(postId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return commentDao.findComment(commentId, postId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getComments(long postId, int page, int size, long neighborhoodId) {
        LOGGER.info("Getting Comments for Post {} from Neighborhood {}", postId, neighborhoodId);

        ValidationUtils.checkPostId(postId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        postDao.findPost(postId, neighborhoodId).orElseThrow(NotFoundException::new);

        return commentDao.getComments(postId, page, size);
    }

    // ---------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public int countComments(long postId) {
        LOGGER.info("Counting Comments for Post {}", postId);

        ValidationUtils.checkPostId(postId);

        return commentDao.countComments(postId);
    }

    @Transactional(readOnly = true)
    public int calculateCommentPages(long postId, int size) {
        LOGGER.info("Calculating Comment for Post {}", postId);

        ValidationUtils.checkPostId(postId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(commentDao.countComments(postId), size);
    }
}
