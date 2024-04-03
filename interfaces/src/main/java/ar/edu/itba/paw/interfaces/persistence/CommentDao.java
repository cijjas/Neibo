package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {

    // -------------------------------------------- COMMENTS INSERT ----------------------------------------------------

    Comment createComment(final String comment, final long neighborId, final long postId);

    // -------------------------------------------- COMMENTS DELETE ----------------------------------------------------
    boolean deleteComment(final long commentId);

    // -------------------------------------------- COMMENTS SELECT ----------------------------------------------------

    Optional<Comment> findComment(long commentId);

    Optional<Comment> findComment(long commentId, long postId, long neighborhoodId);

    List<Comment> getComments(long postId, int offset, int size);

    // ---------------------------------------------------

    int countComments(long postId);
}
