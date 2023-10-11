package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Comment createComment(final String comment, final long neighborId, final long postId);

    Optional<Comment> findCommentById(long id);

    List<Comment> findCommentsByPostId(long id, int page, int size);

    int getCommentsCountByPostId(long id);

    int getTotalPostPages(long id, int size);
}
