package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post createPost(final String title, final String description, final long neighborId, final long channelId, final byte[] imageFile);

    Post createAdminPost(final String title, final String description, final long neighborId, final int channelId, final byte[] imageFile);

    List<Post> getPosts(int offset, int limit);

    List<Post> getPostsByDate(final String order, int offset, int limit);

    List<Post> getPostsByTag(final String tag, int offset, int limit);

    List<Post> getPostsByChannel(final String channel, int offset, int limit);

    Optional<Post> findPostById(long id);

    int getTotalPostsCount();

}
