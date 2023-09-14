package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Post;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final EmailService emailService;
    private final PostService ps;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao, EmailService emailService, PostService ps) {
        this.commentDao = commentDao;
        this.emailService = emailService;
        this.ps = ps;
    }

    @Override
    public Optional<List<Comment>> findCommentsByPostId(long id) {
        return commentDao.findCommentsByPostId(id);
    }

    @Override
    public Comment createComment(String comment, long neighborId, long postId) {

        try {
            //busco al dueno del post:
            Post post = ps.findPostById(postId).orElse(null);
            Neighbor neighbor = post.getNeighbor();
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", neighbor.getName());
            variables.put("postTitle", post.getTitle());
            variables.put("postPath", "http://localhost:8080/posts/" + post.getPostId());

            emailService.sendMessageUsingThymeleafTemplate(neighbor.getMail(), "New comment", null, variables);

        } catch (MessagingException e) {
            // Handle the exception, e.g., log it
            e.printStackTrace();
        }

        return commentDao.createComment(comment, neighborId, postId);
    }
}
