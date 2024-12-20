package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {

    // -------------------------------------------- COMMENTS INSERT ----------------------------------------------------

    Comment createComment(long userId, long postId, String comment);

    // -------------------------------------------- COMMENTS SELECT ----------------------------------------------------

    Optional<Comment> findComment(long commentId);

    Optional<Comment> findComment(long neighborhoodId, long postId, long commentId);

    List<Comment> getComments(long neighborhoodId, long postId, int page, int size);

    int countComments(long neighborhoodId, long postId);

    // -------------------------------------------- COMMENTS DELETE ----------------------------------------------------

    boolean deleteComment(long neighborhoodId, long postId, long commentId);
}
