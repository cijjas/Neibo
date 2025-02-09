package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Post createPost(long neighborhoodId, long userId, String title, String description, long channelId, List<Long> tagIds, Long imageId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Post> findPost(long neighborhoodId, long postId);

    List<Post> getPosts(long neighborhoodId, Long userId, Long channelId, List<Long> tagIds, Long postStatusId, int page, int size);

    int countPosts(long neighborhoodId, Long userId, Long channelId, List<Long> tagIds, Long postStatusId);

    int calculatePostPages(long neighborhoodId, Long userId, Long channelId, List<Long> tagIds, Long postStatusId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deletePost(long neighborhoodId, long postId);
}
