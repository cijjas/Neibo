package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {

    // -------------------------------------------- COMMENTS INSERT ----------------------------------------------------

    Comment createComment(final String comment, final long neighborId, final long postId);


    // -------------------------------------------- COMMENTS SELECT ----------------------------------------------------


    Optional<Comment> findCommentById(long id);

    List<Comment> getCommentsByPostId(long id, int offset, int size);

    int getCommentsCountByPostId(long id);

    //Optional<List<Comment>> findCommentsByUserId(long id);
}
