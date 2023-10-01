package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Tag;
import enums.SortOrder;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface PostService {

    // -----------------------------------------------------------------------------------------------------------------

    Post createPost(final String title, final String description, final long neighborId, final long channelId, String tags, final MultipartFile imageFile);

    // It is not entirely necessary to have the neighborhoodId here but makes it easier for the mail sending
    Post createAdminPost(final long neighborhoodId, final String title, final String description, final long neighborId, final int channelId, String tags, final MultipartFile imageFile);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Post> findPostById(long id);

    List<Post> getPostsByCriteria(String channel, int page, int size, SortOrder date, List<String> tags, long neighborhoodId);

    int getPostsCountByCriteria(String channel, List<String> tags, long neighborhoodId);

    int getTotalPages(String channel, int size, List<String> tags, long neighborhoodId);
}
