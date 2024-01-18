package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CommentDao;
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
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao, EmailService emailService, PostService postService, UserService userService) {
        this.commentDao = commentDao;
        this.emailService = emailService;
        this.postService = postService;
        this.userService = userService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Comment createComment(String comment, long userId, long postId) {
        LOGGER.info("Creating Comment {} from User {} for Post {}", comment, userId, postId);

        Post post = postService.findPost(postId).orElseThrow(()-> new NotFoundException("Post Not Found"));
        emailService.sendNewCommentMail(post, userService.getNeighborsSubscribed(postId));
        return commentDao.createComment(comment, userId, postId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findComment(long commentId, long postId) {
        LOGGER.info("Finding Comment {}", commentId);

        ValidationUtils.checkCommentId(commentId);
        ValidationUtils.checkPostId(postId);

        return commentDao.findComment(commentId, postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getComments(long postId, int page, int size) {
        LOGGER.info("Finding Comments for Post {}", postId);

        ValidationUtils.checkPostId(postId);
        ValidationUtils.checkPageAndSize(page, size);

        return commentDao.getComments(postId, page, size);
    }

    // ---------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public int countComments(long postId) {
        LOGGER.info("Getting Quantity of Comments for Post {}", postId);

        ValidationUtils.checkPostId(postId);

        return commentDao.countComments(postId);
    }

    @Transactional(readOnly = true)
    public int calculateCommentPages(long postId, int size) {
        LOGGER.info("Getting Total Comment Pages for size {}", size);

        ValidationUtils.checkPostId(postId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(commentDao.countComments(postId), size);
    }
}
