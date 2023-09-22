package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.NeighborService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Post;
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
    private final NeighborService ns;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao, EmailService emailService, PostService ps, NeighborService ns){
        this.commentDao = commentDao;
        this.emailService = emailService;
        this.ps = ps;
        this.ns = ns;
    }

    @Override
    public Optional<List<Comment>> findCommentsByPostId(long id) {
        return commentDao.findCommentsByPostId(id);
    }

    @Override
    public Comment createComment(String comment, long neighborId, long postId) {


        //busco al dueno del post:
        Post post = ps.findPostById(postId).orElse(null);
        assert post != null;
        Neighbor neighbor = post.getNeighbor();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", neighbor.getName());
        variables.put("postTitle", post.getTitle());
        variables.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
        emailService.sendMessageUsingThymeleafTemplate(neighbor.getMail(), "New comment", "template-thymeleaf.html", variables);

        for(Neighbor n : ns.getNeighborsSubscribedByPostId(postId)) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", n.getName());
            vars.put("postTitle", post.getTitle());
            vars.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
            emailService.sendMessageUsingThymeleafTemplate(n.getMail(), "New comment", "template-thymeleaf.html", vars);
        }



        return commentDao.createComment(comment, neighborId, postId);
    }
}
