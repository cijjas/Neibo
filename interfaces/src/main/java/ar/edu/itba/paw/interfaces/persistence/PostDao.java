package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {

    // ------------------------------------------------ POSTS INSERT ---------------------------------------------------

    Post createPost(long userId, String title, String description, long channelId, long imageId);

    // ------------------------------------------------ POSTS SELECT ---------------------------------------------------

    Optional<Post> findPost(long neighborhoodId, long postId);

    List<Post> getPosts(long neighborhoodId, Long userId, Long channelId, List<Long> tagIds, Long postStatusId, int page, int size);

    int countPosts(long neighborhoodId, Long userId, Long channelId, List<Long> tags, Long postStatusId);

    // ------------------------------------------------ POSTS DELETE ---------------------------------------------------

    boolean deletePost(long neighborhoodId, long postId);
}
