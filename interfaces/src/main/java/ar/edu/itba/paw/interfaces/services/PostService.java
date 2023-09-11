package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post createPost(final String title, final String description, final long neighborId, final long channelId, final String imageFile);

    List<Post> getAllPosts();

    List<Post> getAllPostsByDate(final String order);

    List<Post> getAllPostsByTag(final String tag);

    List<Post> getAllPostsByChannel(final String channel);

    Optional<Post> findPostById(long id);


}
