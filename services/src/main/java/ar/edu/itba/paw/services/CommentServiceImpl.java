package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Comment;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final EmailService emailService;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao, EmailService emailService) {
        this.commentDao = commentDao;
        this.emailService = emailService;
    }

    @Override
    public Optional<List<Comment>> findCommentsByPostId(long id) {
        return commentDao.findCommentsByPostId(id);
    }

    @Override
    public Comment createComment(String comment, long neighborId, long postId) {

//        emailService.sendSimpleMessage("flopezmenardi@itba.edu.ar", "New comment", "A new comment has been created");
        try {
            // ...
//            emailService.sendWelcomeEmail("flopezmenardi@itba.edu.ar", "felix");
            emailService.sendMessageUsingThymeleafTemplate("flopezmenardi@itba.edu.ar", "New comment", null);
        } catch (MessagingException e) {
            // Handle the exception, e.g., log it
            e.printStackTrace();
        }

        return commentDao.createComment(comment, neighborId, postId);
    }
}
