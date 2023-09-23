package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {
    Post createPost(final String title, final String description, final long neighborId, final long channelId, final byte[] imageFile);

    List<Post> getPostsByCriteria(String channel, String tag, String order, int offset, int limit);

    int getTotalPostsCountByCriteria(String channel, String tag);

    Optional<Post> findPostById(long id);

}
