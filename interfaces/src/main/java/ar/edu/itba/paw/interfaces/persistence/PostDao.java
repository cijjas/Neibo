package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface PostDao {
    Post createPost(final String title, final String description, final long neighborId, final long channelId, final byte[] imageFile);

    List<Post> getPosts();

    List<Post> getPostsByDate(String order);

    List<Post> getPostsByTag(String tag);

    List<Post> getPostsByChannel(String channel);

    Optional<Post> findPostById(long id);
}
