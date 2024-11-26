package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Post createPost(String title, String description, long userId, long channelId, List<Long> tagIds, Long imageId, long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Post> findPost(long postId);

    Optional<Post> findPost(long postId, long neighborhoodId);

    List<Post> getPosts(Long channelId, int page, int size, List<Long> tagIds, long neighborhoodId, Long postStatusId, Long userId);

    int calculatePostPages(Long channelId, int size, List<Long> tagIds, long neighborhoodId, Long postStatusId, Long userId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deletePost(final long postId, final long neighborhoodId);
}
