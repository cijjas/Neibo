package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Comment createComment(final long userId, final long postId, final String comment);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Comment> findComment(long commentId);

    Optional<Comment> findComment(long neighborhoodId, long postId, long commentId);

    List<Comment> getComments(long neighborhoodId, long postId, int size, int page);

    int calculateCommentPages(long postId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteComment(final long neighborhoodId, final long postId, final long commentId);
}
