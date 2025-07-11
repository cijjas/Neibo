package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Comment createComment(long userId, long postId, String comment);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Comment> findComment(long neighborhoodId, long postId, long commentId);

    List<Comment> getComments(long neighborhoodId, long postId, int size, int page);

    int countComments(long neighborhoodId, long postId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteComment(long neighborhoodId, long postId, long commentId);
}
