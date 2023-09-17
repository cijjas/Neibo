package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {
    Post createPost(final String title, final String description, final long neighborId, final long channelId, final byte[] imageFile);

    List<Post> getPosts(int offset, int limit);

    List<Post> getPostsByDate(String order, int offset, int limit);

    List<Post> getPostsByTag(String tag, int offset, int limit);

    List<Post> getPostsByChannel(String channel, int offset, int limit);

    Optional<Post> findPostById(long id);

    int getTotalPostsCount(String tag);
}
