package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.models.Entities.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {

    // ------------------------------------------------ POSTS INSERT ---------------------------------------------------

    Post createPost(final String title, final String description, final long neighborId, final long channelId, final long imageId);

    // ------------------------------------------------ POSTS SELECT ---------------------------------------------------

    Optional<Post> findPost(long postId);

    List<Post> getPosts(String channel, int page, int size, List<String> tags, long neighborhoodId, PostStatus hot, Long userId);

    // ---------------------------------------------------

    int countPosts(String channel, List<String> tags, long neighborhoodId, PostStatus hot, Long userId);
}
