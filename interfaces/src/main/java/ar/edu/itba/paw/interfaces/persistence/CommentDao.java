package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {

    // -------------------------------------------- COMMENTS INSERT ----------------------------------------------------

    List<Comment> findCommentsByPostId(long id, int page, int size);

    int getCommentsCountByPostId(long id);

    // -------------------------------------------- COMMENTS SELECT ----------------------------------------------------

    Comment createComment(final String comment, final long neighborId, final long postId);

    Optional<Comment> findCommentById(long id);

    //Optional<List<Comment>> findCommentsByUserId(long id);
}
