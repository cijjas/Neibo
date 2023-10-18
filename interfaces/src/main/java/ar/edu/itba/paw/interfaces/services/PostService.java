package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.models.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Post createPost(final String title, final String description, final long neighborId, final long channelId, String tags, final MultipartFile imageFile);

    Post createAdminPost(final long neighborhoodId, final String title, final String description, final long neighborId, final int channelId, String tags, final MultipartFile imageFile);

    Post createWorkerPost(final String title, final String description, final long neighborId, final MultipartFile imageFile);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Post> findPostById(long id);

    List<Post> getWorkerPostsByCriteria(String channel, int page, int size, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId);

    List<Post> getPostsByCriteria(String channel, int page, int size, List<String> tags, long neighborhoodId, PostStatus postStatus);

    int getPostsCountByCriteria(String channel, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId);

    int getTotalPages(String channel, int size, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId);
}
