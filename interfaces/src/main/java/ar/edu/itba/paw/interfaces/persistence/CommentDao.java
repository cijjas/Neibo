package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {

    // -------------------------------------------- COMMENTS INSERT ----------------------------------------------------

    Comment createComment(final String comment, final long neighborId, final long postId);

    // -------------------------------------------- COMMENTS SELECT ----------------------------------------------------

    Optional<Comment> findComment(long commentId, long postId);

    List<Comment> getComments(long postId, int offset, int size);

    // ---------------------------------------------------

    int countComments(long postId);
}
