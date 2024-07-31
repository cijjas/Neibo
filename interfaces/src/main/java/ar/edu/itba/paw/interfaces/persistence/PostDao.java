package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {

    // ------------------------------------------------ POSTS INSERT ---------------------------------------------------

    Post createPost(final String title, final String description, final long neighborId, final long channelId, final long imageId);

    // ------------------------------------------------ POSTS SELECT ---------------------------------------------------

    Optional<Post> findPost(long postId);

    Optional<Post> findPost(long postId, long neighborhoodId);

    List<Post> getPosts(Long channelId, int page, int size, List<Long> tagIds, long neighborhoodId, Long postStatusId, Long userId);

    int countPosts(Long channelId, List<Long> tags, long neighborhoodId, Long postStatusId, Long userId);

    // ------------------------------------------------ POSTS DELETE ---------------------------------------------------

    boolean deletePost(final long postId);
}
