package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.models.Entities.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Post createPost(final String title, final String description, final long neighborId, final long channelId, String tags, final MultipartFile imageFile);

    Post createAdminPost(final long neighborhoodId, final String title, final String description, final long neighborId, final int channelId, String tags, final MultipartFile imageFile);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Post> findPost(long postId);

    List<Post> getPosts(String channel, int page, int size, List<String> tags, long neighborhoodId, String postStatus, Long userId);

    // ---------------------------------------------------

    int countPosts(String channel, List<String> tags, long neighborhoodId, String postStatus, Long userId);

    int calculatePostPages(String channel, int size, List<String> tags, long neighborhoodId, String postStatus, Long userId);
}
