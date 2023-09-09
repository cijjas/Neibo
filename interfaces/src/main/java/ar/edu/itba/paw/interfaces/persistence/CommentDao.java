package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;

import java.util.List;
import java.util.Optional;

public interface CommentDao {
    Optional<List<Comment>> findCommentsByPostId(long id);
}
