package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Tag;
import enums.SortOrder;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface PostService {
    Post createPost(final String title, final String description, final long neighborId, final long channelId, String tags, final MultipartFile imageFile);

    Post createAdminPost(final String title, final String description, final long neighborId, final int channelId, String tags, final MultipartFile imageFile);

    Optional<Post> findPostById(long id);

    List<Post> getPostsByCriteria(String channel, int page, int size, SortOrder date, List<String> tags);

    int getPostsCountByCriteria(String channel, List<String> tags);

    int getTotalPages(String channel, int size, List<String> tags);

    // --- voy a asesinar lo que esta aca abajo
/*

    List<Post> getPostsByDate(final String order, int offset, int limit);

    List<Post> getPostsByTag(final String tag, int offset, int limit);

    List<Post> getPostsByChannel(final String channel, int offset, int limit);

    List<Post> getPostsByChannelAndDate(final String channel, final String order, int offset, int limit);

    List<Post> getPostsByChannelAndDateAndTag(final String channel, final String order, final String tag, int offset, int limit);



    int getTotalPostsCount();

    int getTotalPostsCountInChannel(String channel);

    int getTotalPostsCountWithTag(String tag);

    int getTotalPostsCountInChannelWithTag(String channel, String tag);
*/

}
