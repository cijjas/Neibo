package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface PostDao {
    Post create(final String title, final String description, final long neighborId);

    List<Post> getAllPosts();

    List<Post> getAllPostsByDate(String order);

    List<Post> getAllPostsByTag(String tag);

    Optional<Post> findPostById(long id);
}
