package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.Post;

import java.util.List;

public interface PostService {
    Post createPost(final String title, final String description, final long neighborId);

    List<Post> getAllPosts();
}
