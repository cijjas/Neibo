package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Post createPost(final String title, final String description, final String userURN, final String channelURN, List<String> tagURIs, final String imageURN);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Post> findPost(long postId);

    Optional<Post> findPost(long postId, long neighborhoodId);

    List<Post> getPosts(String channelURN, int page, int size, List<String> tagURNs, long neighborhoodId, String postStatusURN, String userURN);

    // ---------------------------------------------------

    int calculatePostPages(String channelURN, int size, List<String> tagURNs, long neighborhoodId, String postStatusURN, String userURN);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deletePost(final long postId, final long neighborhoodId);
}
