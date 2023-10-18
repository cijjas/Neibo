package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.enums.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final EmailService emailService;
    private final PostService postService;
    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao, EmailService emailService, PostService postService, UserService userService){
        this.commentDao = commentDao;
        this.emailService = emailService;
        this.postService = postService;
        this.userService = userService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Comment createComment(String comment, long neighborId, long postId) {
        LOGGER.info("Creating Comment {} from User {} for Post {}", comment, neighborId, postId);

        Post post = postService.findPostById(postId).orElse(null);
        assert post != null;
        User user = post.getUser();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("postTitle", post.getTitle());
        variables.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
        boolean isEnglish = user.getLanguage() == Language.ENGLISH;
        emailService.sendMessageUsingThymeleafTemplate(user.getMail(), isEnglish? "New comment" : "Nuevo Comentario", isEnglish? "comment-template_en.html" : "comment-template_es.html", variables);

        for(User n : userService.getNeighborsSubscribedByPostId(postId)) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", n.getName());
            vars.put("postTitle", post.getTitle());
            vars.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
            emailService.sendMessageUsingThymeleafTemplate(user.getMail(), isEnglish? "New comment" : "Nuevo Comentario", isEnglish? "comment-template_en.html" : "comment-template_es.html", vars);
        }

        return commentDao.createComment(comment, neighborId, postId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findCommentById(long id){
        LOGGER.info("Finding Comment {}", id);
        return commentDao.findCommentById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(long id, int page, int size) {
        LOGGER.info("Finding Comments for Post {}", id);
        return commentDao.getCommentsByPostId(id,page,size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCommentsCountByPostId(long id) {
        LOGGER.info("Getting Quantity of Comments for Post {}", id);
        return commentDao.getCommentsCountByPostId(id);
    }

    @Transactional(readOnly = true)
    public int getTotalCommentPages(long id, int size) {
        LOGGER.info("Getting Total Comment Pages for size {}", size);
        return (int) Math.ceil((double) commentDao.getCommentsCountByPostId(id) / size);
    }




}
