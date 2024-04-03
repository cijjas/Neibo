package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.models.Entities.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {

    // ------------------------------------------------ POSTS INSERT ---------------------------------------------------

    Post createPost(final String title, final String description, final long neighborId, final long channelId, final long imageId);

    // ------------------------------------------------ POSTS DELETE ---------------------------------------------------
    boolean deletePost(final long postId);

    // ------------------------------------------------ POSTS SELECT ---------------------------------------------------

    Optional<Post> findPost(long postId);

    Optional<Post> findPost(long postId, long neighborhoodId);

    List<Post> getPosts(String channel, int page, int size, List<String> tags, long neighborhoodId, String hot, Long userId);

    // ---------------------------------------------------

    int countPosts(String channel, List<String> tags, long neighborhoodId, String hot, Long userId);
}
