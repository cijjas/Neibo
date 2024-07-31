package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Comment createComment(final String comment, final String userURN, final long postId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Comment> findComment(long commentId);

    Optional<Comment> findComment(long commentId, long postId, long neighborhoodId);

    List<Comment> getComments(long postId, int page, int size, long neighborhoodId);

    // ---------------------------------------------------

    int calculateCommentPages(long postId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteComment(final long commentId);
}
