package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.List;

public interface PostDao {
    Post create(final String title, final String description, final long neighborId);

    List<Post> getAllPosts();
}
