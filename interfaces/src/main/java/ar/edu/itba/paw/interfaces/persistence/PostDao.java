package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {
    Post createPost(final String title, final String description, final long neighborId, final long channelId, final byte[] imageFile);

    List<Post> getPostsByCriteria(String channel, int page, int size, String date, List<String> tags);

    public int getPostsCountByCriteria(String channel, List<String> tags);

    Optional<Post> findPostById(long id);

}
